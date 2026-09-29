package com.traineta.service;

import com.traineta.dto.FeatureRequest;
import com.traineta.entity.Station;
import com.traineta.entity.TrainPosition;
import com.traineta.entity.TrainSchedule;
import com.traineta.exception.ResourceNotFoundException;
import com.traineta.repository.TrainPositionRepository;
import com.traineta.repository.TrainScheduleRepository;
import com.traineta.util.DistanceCalculator;
import com.traineta.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeatureService {

    private static final Logger log = LoggerFactory.getLogger(FeatureService.class);

    private final TrainPositionRepository trainPositionRepository;
    private final TrainScheduleRepository trainScheduleRepository;
    private final LiveTrainService liveTrainService;
    private final WeatherService weatherService;
    private final CongestionService congestionService;

    @Autowired
    public FeatureService(TrainPositionRepository trainPositionRepository,
                          TrainScheduleRepository trainScheduleRepository,
                          LiveTrainService liveTrainService,
                          WeatherService weatherService,
                          CongestionService congestionService) {
        this.trainPositionRepository = trainPositionRepository;
        this.trainScheduleRepository = trainScheduleRepository;
        this.liveTrainService = liveTrainService;
        this.weatherService = weatherService;
        this.congestionService = congestionService;
    }

    public FeatureService(TrainPositionRepository trainPositionRepository,
                          TrainScheduleRepository trainScheduleRepository) {
        this(trainPositionRepository, trainScheduleRepository, null, null, null);
    }

    public FeatureRequest buildFeaturesForTrain(String trainNumber) {
        log.info("Building ML feature vector for train: {}", trainNumber);

        // 1. Fetch latest train position
        TrainPosition position = trainPositionRepository.findFirstByTrain_TrainNumberOrderByTimestampDesc(trainNumber)
                .orElseGet(() -> {
                    if (liveTrainService != null) {
                        liveTrainService.fetchAndSaveLiveStatus(trainNumber);
                        return trainPositionRepository.findFirstByTrain_TrainNumberOrderByTimestampDesc(trainNumber)
                                .orElseThrow(() -> new ResourceNotFoundException("No live train position data available for train: " + trainNumber));
                    }
                    throw new ResourceNotFoundException("No live train position data available for train: " + trainNumber);
                });

        // 2. Fetch train schedule ordered by stop sequence (guarantees correct stopsRemaining calc)
        List<TrainSchedule> schedules = trainScheduleRepository.findByTrain_TrainNumberOrderByStopNumberAsc(trainNumber);

        // 3. Derived calculations
        int stopsRemaining = 0;
        long scheduledRemainingMinutes = 0L;
        double distanceRemaining = 0.0;
        String targetStationId = "DEST";

        if (!schedules.isEmpty()) {
            // Calculate stops remaining based on current station position in schedule
            int currentIdx = -1;
            for (int i = 0; i < schedules.size(); i++) {
                Station st = schedules.get(i).getStation();
                if (st != null && st.getStationCode().equalsIgnoreCase(position.getCurrentStation())) {
                    currentIdx = i;
                    break;
                }
            }

            if (currentIdx != -1) {
                stopsRemaining = Math.max(0, schedules.size() - 1 - currentIdx);
            } else {
                stopsRemaining = schedules.size();
            }

            // Calculate remaining scheduled minutes to destination
            TrainSchedule destSchedule = schedules.get(schedules.size() - 1);
            if (destSchedule.getScheduledArrival() != null) {
                LocalDateTime refTime = position.getTimestamp() != null ? position.getTimestamp() : LocalDateTime.now();
                scheduledRemainingMinutes = TimeUtil.calculateMinutesBetween(refTime, destSchedule.getScheduledArrival());
            }

            // Set dynamic target station code
            Station destStation = destSchedule.getStation();
            if (destStation != null && destStation.getStationCode() != null) {
                targetStationId = destStation.getStationCode();
            }

            // Calculate distance remaining using Haversine formula if coordinates exist
            if (position.getLatitude() != null && position.getLongitude() != null &&
                destStation != null && destStation.getLatitude() != null && destStation.getLongitude() != null) {
                distanceRemaining = DistanceCalculator.calculateDistanceKm(
                        position.getLatitude(), position.getLongitude(),
                        destStation.getLatitude(), destStation.getLongitude()
                );
            }
        } else {
            if (position.getTrain() != null && position.getTrain().getDestinationStation() != null) {
                targetStationId = position.getTrain().getDestinationStation();
            } else if (position.getNextStation() != null && !position.getNextStation().isBlank()) {
                targetStationId = position.getNextStation();
            }
            log.warn("Historical schedule data is currently not available for train {}. Defaulting schedule features.", trainNumber);
        }

        LocalDateTime timestamp = position.getTimestamp() != null ? position.getTimestamp() : LocalDateTime.now();

        // Fetch real-time weather and congestion features
        Double rainfallMm = weatherService != null ? weatherService.getRainfallMm(position.getCurrentStation()) : null;
        Double congestionScore = congestionService != null ? congestionService.getCongestionScore(position.getCurrentStation()) : null;

        double currentSpeed = position.getSpeed() != null ? position.getSpeed().doubleValue() : 0.0;

        // Calculate 5-minute rolling average speed from train position history
        List<TrainPosition> recentPositions = trainPositionRepository
                .findByTrain_TrainNumberAndTimestampGreaterThanEqualOrderByTimestampDesc(trainNumber, timestamp.minusMinutes(5));

        double averageSpeed5Min = currentSpeed;
        if (recentPositions != null && !recentPositions.isEmpty()) {
            averageSpeed5Min = recentPositions.stream()
                    .filter(p -> p.getSpeed() != null)
                    .mapToDouble(p -> p.getSpeed().doubleValue())
                    .average()
                    .orElse(currentSpeed);
        }

        // 4. Construct Feature DTO
        FeatureRequest featureRequest = FeatureRequest.builder()
                .trainNumber(trainNumber)
                .targetStationId(targetStationId)
                .currentDelayMinutes(position.getDelayMinutes() != null ? position.getDelayMinutes() : 0)
                .currentSpeed(currentSpeed)
                .averageSpeedLast5Minutes(averageSpeed5Min)
                .latitude(position.getLatitude() != null ? position.getLatitude() : 0.0)
                .longitude(position.getLongitude() != null ? position.getLongitude() : 0.0)
                .distanceRemaining(distanceRemaining)
                .scheduledRemainingMinutes(scheduledRemainingMinutes)
                .stopsRemaining(stopsRemaining)
                .hourOfDay(TimeUtil.extractHourOfDay(timestamp))
                .dayOfWeek(TimeUtil.extractDayOfWeek(timestamp))
                .rainfallMm(rainfallMm)
                .congestionScore(congestionScore)
                .build();

        // 5. Validation
        validateFeatures(featureRequest);

        log.info("Successfully built ML features for train {}: {}", trainNumber, featureRequest);
        return featureRequest;
    }

    private void validateFeatures(FeatureRequest features) {
        if (features.getLatitude() < -90.0 || features.getLatitude() > 90.0) {
            throw new IllegalArgumentException("Invalid latitude value: " + features.getLatitude() + ". Must be between -90 and 90.");
        }
        if (features.getLongitude() < -180.0 || features.getLongitude() > 180.0) {
            throw new IllegalArgumentException("Invalid longitude value: " + features.getLongitude() + ". Must be between -180 and 180.");
        }
        if (features.getCurrentSpeed() < 0.0) {
            throw new IllegalArgumentException("Invalid current speed: " + features.getCurrentSpeed() + ". Speed cannot be negative.");
        }
        if (features.getStopsRemaining() < 0) {
            throw new IllegalArgumentException("Invalid stops remaining: " + features.getStopsRemaining() + ". Cannot be negative.");
        }
        if (features.getDistanceRemaining() < 0.0) {
            throw new IllegalArgumentException("Invalid distance remaining: " + features.getDistanceRemaining() + ". Cannot be negative.");
        }
    }
}
