package com.traineta.service.impl;

import com.traineta.client.MlApiClient;
import com.traineta.dto.FeatureRequest;
import com.traineta.dto.MlPredictionResponse;
import com.traineta.dto.PythonMlRequest;
import com.traineta.dto.PythonMlResponse;
import com.traineta.service.MlPredictionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Service
@Primary
public class RealMlPredictionService implements MlPredictionService {

    private static final Logger log = LoggerFactory.getLogger(RealMlPredictionService.class);
    private final MlApiClient mlApiClient;

    public RealMlPredictionService(MlApiClient mlApiClient) {
        this.mlApiClient = mlApiClient;
    }

    @Override
    public MlPredictionResponse predict(FeatureRequest features) {
        if (features == null || features.getTrainNumber() == null) {
            throw new IllegalArgumentException("Feature request payload cannot be null");
        }

        log.info("[REAL ML SERVICE] Preparing prediction request for Python XGBoost service for train {}", features.getTrainNumber());

        double currentSpeed = features.getCurrentSpeed() != null ? features.getCurrentSpeed() : 0.0;
        double avgSpeed5Min = features.getAverageSpeedLast5Minutes() != null ? features.getAverageSpeedLast5Minutes() : currentSpeed;
        String targetStation = (features.getTargetStationId() != null && !features.getTargetStationId().isBlank())
                ? features.getTargetStationId()
                : "DEST";

        PythonMlRequest pythonRequest = PythonMlRequest.builder()
                .trainId(features.getTrainNumber())
                .targetStationId(targetStation)
                .observedAt(OffsetDateTime.now().toString())
                .currentSpeedKmph(currentSpeed)
                .averageSpeedLast5Minutes(avgSpeed5Min)
                .currentDelayMinutes(features.getCurrentDelayMinutes() != null ? features.getCurrentDelayMinutes().doubleValue() : 0.0)
                .distanceToTargetKm(features.getDistanceRemaining() != null ? features.getDistanceRemaining() : 0.0)
                .scheduledTimeToTargetMinutes(features.getScheduledRemainingMinutes() != null ? features.getScheduledRemainingMinutes().doubleValue() : 0.0)
                .rainfallMm(features.getRainfallMm())
                .congestionScore(features.getCongestionScore())
                .historicalSectionAverageMinutes(null)
                .build();

        PythonMlResponse pythonResponse = mlApiClient.predictEta(pythonRequest).block();

        if (pythonResponse != null && pythonResponse.getPredictedRemainingMinutes() != null) {
            log.info("[REAL ML SERVICE] Received prediction from Python FastAPI for train {}: {} mins",
                    features.getTrainNumber(), pythonResponse.getPredictedRemainingMinutes());

            double confidence = (pythonResponse.getConfidenceScore() != null)
                    ? pythonResponse.getConfidenceScore()
                    : 0.88;

            return MlPredictionResponse.builder()
                    .trainNumber(features.getTrainNumber())
                    .predictedRemainingMinutes(pythonResponse.getPredictedRemainingMinutes())
                    .confidenceScore(confidence)
                    .modelVersion(pythonResponse.getModelVersion() != null ? pythonResponse.getModelVersion() : "hybrid-eta-v2-xgb-dual")
                    .predictionSource("REAL_XGBOOST")
                    .predictionTimestamp(LocalDateTime.now())
                    .build();
        }

        log.warn("[REAL ML SERVICE] Python ML service returned null or timed out. Falling back to internal mock formula for train {}", features.getTrainNumber());
        long baseScheduled = features.getScheduledRemainingMinutes() != null ? features.getScheduledRemainingMinutes() : 120L;
        int delay = features.getCurrentDelayMinutes() != null ? features.getCurrentDelayMinutes() : 0;
        double predictedRemaining = Math.max(0.0, (double) baseScheduled + delay);

        return MlPredictionResponse.builder()
                .trainNumber(features.getTrainNumber())
                .predictedRemainingMinutes(predictedRemaining)
                .confidenceScore(0.70)
                .modelVersion("v0.1-fallback")
                .predictionSource("FALLBACK_MOCK")
                .predictionTimestamp(LocalDateTime.now())
                .build();
    }
}
