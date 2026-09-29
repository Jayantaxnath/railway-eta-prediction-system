package com.traineta.service;

import com.traineta.dto.EtaResponse;
import com.traineta.dto.FeatureRequest;
import com.traineta.dto.MlPredictionResponse;
import com.traineta.entity.EtaPrediction;
import com.traineta.entity.Train;
import com.traineta.exception.EtaCalculationException;
import com.traineta.exception.ResourceNotFoundException;
import com.traineta.repository.EtaPredictionRepository;
import com.traineta.repository.TrainRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EtaService {

    private static final Logger log = LoggerFactory.getLogger(EtaService.class);

    private final FeatureService featureService;
    private final MlPredictionService mlPredictionService;
    private final EtaPredictionRepository etaPredictionRepository;
    private final TrainRepository trainRepository;

    public EtaService(FeatureService featureService,
                      MlPredictionService mlPredictionService,
                      EtaPredictionRepository etaPredictionRepository,
                      TrainRepository trainRepository) {
        this.featureService = featureService;
        this.mlPredictionService = mlPredictionService;
        this.etaPredictionRepository = etaPredictionRepository;
        this.trainRepository = trainRepository;
    }

    @Transactional
    public EtaResponse generateAndSaveEta(String trainNumber) {
        log.info("Generating ETA prediction pipeline for train: {}", trainNumber);

        // 1. Build ML Features
        FeatureRequest features = featureService.buildFeaturesForTrain(trainNumber);

        // 2. Call ML Prediction Service (Mock or Real)
        MlPredictionResponse prediction = mlPredictionService.predict(features);

        if (prediction == null || prediction.getPredictedRemainingMinutes() == null) {
            throw new EtaCalculationException("Received null prediction from ML prediction service for train: " + trainNumber);
        }

        if (prediction.getPredictedRemainingMinutes() < 0.0) {
            throw new EtaCalculationException("Predicted remaining minutes cannot be negative: " + prediction.getPredictedRemainingMinutes());
        }

        // 3. Compute predicted ETA timestamp
        LocalDateTime baseTimestamp = prediction.getPredictionTimestamp() != null ? prediction.getPredictionTimestamp() : LocalDateTime.now();
        long remainingMinutes = Math.round(prediction.getPredictedRemainingMinutes());
        LocalDateTime predictedEta = baseTimestamp.plusMinutes(remainingMinutes);

        // 4. Fetch/Save Train Entity
        Train train = trainRepository.findById(trainNumber)
                .orElseGet(() -> trainRepository.save(new Train(trainNumber, "Train " + trainNumber, "UNKNOWN", "UNKNOWN")));

        // 5. Build and Save EtaPrediction Entity
        EtaPrediction entity = EtaPrediction.builder()
                .train(train)
                .predictionTimestamp(baseTimestamp)
                .currentDelayMinutes(features.getCurrentDelayMinutes())
                .predictedRemainingMinutes(prediction.getPredictedRemainingMinutes())
                .predictedEta(predictedEta)
                .confidenceScore(prediction.getConfidenceScore())
                .modelVersion(prediction.getModelVersion())
                .predictionSource(prediction.getPredictionSource())
                .build();

        EtaPrediction savedEntity = etaPredictionRepository.save(entity);
        log.info("Successfully calculated & saved ETA prediction for train {}: ETA={}", trainNumber, predictedEta);

        return mapToEtaResponse(savedEntity);
    }

    public EtaResponse getLatestEta(String trainNumber) {
        EtaPrediction prediction = etaPredictionRepository.findFirstByTrain_TrainNumberOrderByPredictionTimestampDesc(trainNumber)
                .orElseThrow(() -> new ResourceNotFoundException("No stored ETA predictions found for train: " + trainNumber));
        return mapToEtaResponse(prediction);
    }

    public List<EtaResponse> getEtaHistory(String trainNumber) {
        List<EtaPrediction> predictions = etaPredictionRepository.findByTrain_TrainNumberOrderByPredictionTimestampDesc(trainNumber);
        if (predictions.isEmpty()) {
            throw new ResourceNotFoundException("No stored ETA prediction history found for train: " + trainNumber);
        }
        return predictions.stream().map(this::mapToEtaResponse).toList();
    }

    private EtaResponse mapToEtaResponse(EtaPrediction entity) {
        return EtaResponse.builder()
                .trainNumber(entity.getTrain() != null ? entity.getTrain().getTrainNumber() : null)
                .currentDelayMinutes(entity.getCurrentDelayMinutes())
                .predictedRemainingMinutes(entity.getPredictedRemainingMinutes())
                .predictedEta(entity.getPredictedEta())
                .confidenceScore(entity.getConfidenceScore())
                .predictionSource(entity.getPredictionSource())
                .modelVersion(entity.getModelVersion())
                .predictionTimestamp(entity.getPredictionTimestamp())
                .build();
    }
}
