package com.traineta.service;

import com.traineta.repository.TrainPositionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CongestionService {

    private static final Logger log = LoggerFactory.getLogger(CongestionService.class);
    private static final double MAX_SECTION_CAPACITY = 5.0;

    private final TrainPositionRepository trainPositionRepository;

    public CongestionService(TrainPositionRepository trainPositionRepository) {
        this.trainPositionRepository = trainPositionRepository;
    }

    /**
     * Returns a congestion score between 0.0 (no congestion) and 1.0 (maximum congestion).
     * Calculates active train density within the block section around the station over the last 30 minutes.
     */
    public Double getCongestionScore(String stationCode) {
        if (stationCode == null || stationCode.isBlank()) {
            return 0.0;
        }

        try {
            LocalDateTime thirtyMinsAgo = LocalDateTime.now().minusMinutes(30);
            long activeTrains = trainPositionRepository.countActiveTrainsNearStation(stationCode, thirtyMinsAgo);
            long delayedTrains = trainPositionRepository.countDelayedTrainsNearStation(stationCode, thirtyMinsAgo);

            // Compute weighted congestion score: active trains density + delay impact
            double rawScore = (activeTrains + 0.5 * delayedTrains) / MAX_SECTION_CAPACITY;
            double congestionScore = Math.min(1.0, Math.max(0.0, rawScore));

            log.info("Calculated track congestion for station {}: activeTrains={}, delayedTrains={}, score={}",
                    stationCode, activeTrains, delayedTrains, congestionScore);
            return congestionScore;
        } catch (Exception e) {
            log.warn("Error calculating congestion score for station {}: {}", stationCode, e.getMessage());
            return 0.0;
        }
    }
}
