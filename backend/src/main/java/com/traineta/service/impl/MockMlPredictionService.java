package com.traineta.service.impl;

import com.traineta.dto.FeatureRequest;
import com.traineta.dto.MlPredictionResponse;
import com.traineta.service.MlPredictionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * ==============================================================================
 * PLACEHOLDER ONLY — TO BE REPLACED BY REAL ML API (FastAPI / XGBoost)
 * ==============================================================================
 * This service simulates the future XGBoost model response using a simple
 * deterministic formula based on input features (scheduled remaining minutes + delay).
 */
@Service
public class MockMlPredictionService implements MlPredictionService {

    private static final Logger log = LoggerFactory.getLogger(MockMlPredictionService.class);

    @Override
    public MlPredictionResponse predict(FeatureRequest features) {
        if (features == null || features.getTrainNumber() == null) {
            throw new IllegalArgumentException("Feature request payload cannot be null");
        }

        log.info("[PLACEHOLDER ML SERVICE] Received feature payload for train {}: {}", features.getTrainNumber(), features);

        // Simple deterministic calculation for prototype mock:
        // Predicted Remaining Minutes = Scheduled Remaining Minutes + Current Delay Minutes
        long baseScheduled = features.getScheduledRemainingMinutes() != null ? features.getScheduledRemainingMinutes() : 120L;
        int delay = features.getCurrentDelayMinutes() != null ? features.getCurrentDelayMinutes() : 0;
        double predictedRemaining = Math.max(0.0, (double) baseScheduled + delay);

        MlPredictionResponse response = MlPredictionResponse.builder()
                .trainNumber(features.getTrainNumber())
                .predictedRemainingMinutes(predictedRemaining)
                .confidenceScore(0.85) // Mock confidence score
                .modelVersion("v0.1-mock")
                .predictionSource("MOCK")
                .predictionTimestamp(LocalDateTime.now())
                .build();

        log.info("[PLACEHOLDER ML SERVICE] Returning mock prediction response for train {}: {}", features.getTrainNumber(), response);
        return response;
    }
}
