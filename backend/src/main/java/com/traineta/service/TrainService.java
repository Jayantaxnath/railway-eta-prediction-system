package com.traineta.service;

import com.traineta.client.RailwayApiClient;
import com.traineta.dto.LiveTrainResponse;
import com.traineta.dto.RailwayApiResponse;
import com.traineta.dto.RailwayScheduleResponse;
import com.traineta.dto.TrainResponse;
import com.traineta.entity.Train;
import com.traineta.entity.TrainPosition;
import com.traineta.exception.ResourceNotFoundException;
import com.traineta.repository.TrainPositionRepository;
import com.traineta.repository.TrainRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainService {

    private final TrainRepository trainRepository;
    private final TrainPositionRepository trainPositionRepository;
    private final LiveTrainService liveTrainService;
    private final RailwayApiClient railwayApiClient;

    public TrainService(TrainRepository trainRepository,
                        TrainPositionRepository trainPositionRepository,
                        LiveTrainService liveTrainService,
                        RailwayApiClient railwayApiClient) {
        this.trainRepository = trainRepository;
        this.trainPositionRepository = trainPositionRepository;
        this.liveTrainService = liveTrainService;
        this.railwayApiClient = railwayApiClient;
    }

    public List<TrainResponse> getAllTrains() {
        return trainRepository.findAll().stream()
                .map(this::mapToTrainResponse)
                .toList();
    }

    public TrainResponse getTrainByNumber(String trainNumber) {
        Train train = trainRepository.findById(trainNumber)
                .orElseGet(() -> {
                    liveTrainService.fetchAndSaveLiveStatus(trainNumber);
                    return trainRepository.findById(trainNumber)
                            .orElseThrow(() -> new ResourceNotFoundException("Train not found with number: " + trainNumber));
                });
        return mapToTrainResponse(train);
    }

    public LiveTrainResponse getLatestLivePosition(String trainNumber) {
        TrainPosition position = trainPositionRepository.findFirstByTrain_TrainNumberOrderByTimestampDesc(trainNumber)
                .orElseGet(() -> {
                    liveTrainService.fetchAndSaveLiveStatus(trainNumber);
                    return trainPositionRepository.findFirstByTrain_TrainNumberOrderByTimestampDesc(trainNumber)
                            .orElseThrow(() -> new ResourceNotFoundException("No live position records found for train: " + trainNumber));
                });
        return mapToLiveTrainResponse(position);
    }

    public List<LiveTrainResponse> getStoredPositions(String trainNumber) {
        List<TrainPosition> positions = trainPositionRepository.findByTrain_TrainNumberOrderByTimestampDesc(trainNumber);
        if (positions.isEmpty()) {
            throw new ResourceNotFoundException("No stored positions found for train: " + trainNumber);
        }
        return positions.stream().map(this::mapToLiveTrainResponse).toList();
    }

    public RailwayApiResponse.RailRadarData getLiveTrainData(String trainNumber, String date) {
        RailwayApiResponse response = railwayApiClient.fetchLiveTrainStatus(trainNumber, date).block();
        if (response != null && response.getData() != null) {
            return response.getData();
        }
        return null;
    }

    public RailwayApiResponse.RailRadarData getLiveTrainData(String trainNumber) {
        return getLiveTrainData(trainNumber, null);
    }

    public RailwayScheduleResponse.RailRadarScheduleData getTrainSchedule(String trainNumber) {
        RailwayScheduleResponse response = railwayApiClient.fetchTrainSchedule(trainNumber).block();
        if (response != null && response.getData() != null) {
            return response.getData();
        }
        return null;
    }


    private TrainResponse mapToTrainResponse(Train train) {
        return new TrainResponse(
                train.getTrainNumber(),
                train.getTrainName(),
                train.getSourceStation(),
                train.getDestinationStation()
        );
    }

    private LiveTrainResponse mapToLiveTrainResponse(TrainPosition position) {
        return new LiveTrainResponse(
                position.getTrain().getTrainNumber(),
                position.getCurrentStation(),
                position.getNextStation(),
                position.getLatitude(),
                position.getLongitude(),
                position.getSpeed(),
                position.getDelayMinutes(),
                position.getTimestamp() != null ? position.getTimestamp().toString() : position.getApiUpdateTimestamp()
        );
    }
}

