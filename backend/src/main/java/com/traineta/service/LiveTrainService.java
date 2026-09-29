package com.traineta.service;

import com.traineta.client.RailwayApiClient;
import com.traineta.dto.RailwayApiResponse;
import com.traineta.entity.Train;
import com.traineta.entity.TrainPosition;
import com.traineta.repository.TrainPositionRepository;
import com.traineta.repository.TrainRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LiveTrainService {

    private static final Logger log = LoggerFactory.getLogger(LiveTrainService.class);

    private final RailwayApiClient railwayApiClient;
    private final TrainRepository trainRepository;
    private final TrainPositionRepository trainPositionRepository;

    public LiveTrainService(RailwayApiClient railwayApiClient,
                            TrainRepository trainRepository,
                            TrainPositionRepository trainPositionRepository) {
        this.railwayApiClient = railwayApiClient;
        this.trainRepository = trainRepository;
        this.trainPositionRepository = trainPositionRepository;
    }

    @Transactional
    public void fetchAndSaveLiveStatus(String trainNumber) {
        try {
            RailwayApiResponse response = railwayApiClient.fetchLiveTrainStatus(trainNumber).block();
            if (response == null || response.getData() == null || response.getData().getTrainNumber() == null) {
                log.warn("No data returned from RailRadar API for train {}. Train does not exist or is inactive.", trainNumber);
                return;
            }

            RailwayApiResponse.RailRadarData data = response.getData();

            String srcCode = "UNKNOWN";
            String destCode = "UNKNOWN";

            if (data.getTrain() != null) {
                if (data.getTrain().getSource() != null && data.getTrain().getSource().getCode() != null) {
                    srcCode = data.getTrain().getSource().getCode();
                }
                if (data.getTrain().getDestination() != null && data.getTrain().getDestination().getCode() != null) {
                    destCode = data.getTrain().getDestination().getCode();
                }
            }

            final String finalSrcCode = srcCode;
            final String finalDestCode = destCode;

            // Ensure Train entity exists
            Train train = trainRepository.findById(data.getTrainNumber())
                    .orElseGet(() -> {
                        Train newTrain = new Train(
                                data.getTrainNumber(),
                                data.getTrainName() != null ? data.getTrainName() : "Train " + data.getTrainNumber(),
                                finalSrcCode,
                                finalDestCode
                        );
                        return trainRepository.save(newTrain);
                    });

            // Map DTO to TrainPosition entity
            TrainPosition position = new TrainPosition();
            position.setTrain(train);

            // NOTE: RailRadar API does not expose real-time GPS coordinates.
            // We store the origin station's lat/lng here as a proxy location.
            // The FeatureService uses these for Haversine distance estimation only.
            // When real GPS coordinates become available, update this mapping.
            if (data.getTrain() != null && data.getTrain().getSource() != null) {
                position.setLatitude(data.getTrain().getSource().getLat());
                position.setLongitude(data.getTrain().getSource().getLng());
            } else {
                position.setLatitude(0.0);
                position.setLongitude(0.0);
            }

            position.setSpeed(0); // RailRadar tracks station status
            position.setDelayMinutes(data.getDelayMinutes() != null ? data.getDelayMinutes() : 0);

            if (data.getCurrentLocation() != null) {
                position.setCurrentStation(data.getCurrentLocation().getStationCode());
                if (data.getCurrentLocation().getDelayMinutes() != null) {
                    position.setDelayMinutes(data.getCurrentLocation().getDelayMinutes());
                }
            } else {
                position.setCurrentStation("UNKNOWN");
            }

            position.setNextStation(data.getNextHalt() != null ? data.getNextHalt().getStationCode() : "UNKNOWN");
            position.setPreviousStation(data.getPreviousHalt() != null ? data.getPreviousHalt().getStationCode() : "UNKNOWN");
            position.setJourneyDate(data.getStartDate());
            position.setTimestamp(LocalDateTime.now());
            position.setApiUpdateTimestamp(data.getLastUpdatedAt() != null ? data.getLastUpdatedAt() : LocalDateTime.now().toString());

            trainPositionRepository.save(position);
            log.info("Successfully persisted live position from RailRadar for train {}", trainNumber);
        } catch (Exception e) {
            log.error("Failed to ingest train data for train {}: {}", trainNumber, e.getMessage());
        }
    }

    private void createFallbackPosition(String trainNumber) {
        Train train = trainRepository.findById(trainNumber)
                .orElseGet(() -> trainRepository.save(new Train(trainNumber, "Train " + trainNumber, "SRC", "DEST")));

        TrainPosition position = new TrainPosition();
        position.setTrain(train);
        position.setLatitude(23.2599);
        position.setLongitude(77.4126);
        position.setSpeed(60);
        position.setDelayMinutes(10);
        position.setCurrentStation("BPL");
        position.setNextStation("UJN");
        position.setPreviousStation("HBJ");
        position.setTimestamp(LocalDateTime.now());
        position.setApiUpdateTimestamp(LocalDateTime.now().toString());

        trainPositionRepository.save(position);
        log.info("Created fallback position record for train {}", trainNumber);
    }
}
