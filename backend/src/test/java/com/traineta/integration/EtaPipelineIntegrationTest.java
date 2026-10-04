package com.traineta.integration;

import com.traineta.dto.EtaResponse;
import com.traineta.dto.FeatureRequest;
import com.traineta.dto.MlPredictionResponse;
import com.traineta.entity.Train;
import com.traineta.entity.TrainPosition;
import com.traineta.repository.TrainPositionRepository;
import com.traineta.repository.TrainRepository;
import com.traineta.service.EtaService;
import com.traineta.service.FeatureService;
import com.traineta.service.MlPredictionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * End-to-end integration test for the full ETA prediction pipeline:
 *
 *   TrainPosition (DB) → FeatureService → MlPredictionService → EtaService → EtaPrediction (DB)
 *
 * Uses a real in-memory H2 database via the "test" profile.
 * MlPredictionService is mocked to avoid requiring the Python ML microservice.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EtaPipelineIntegrationTest {

    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private TrainPositionRepository trainPositionRepository;

    @Autowired
    private FeatureService featureService;

    @Autowired
    private EtaService etaService;

    @MockBean
    private MlPredictionService mlPredictionService;

    private static final String TRAIN_NUMBER = "12919";

    @BeforeEach
    void setUp() {
        // Seed a Train entity
        Train train = new Train(TRAIN_NUMBER, "Malwa SF Express", "INDB", "NDLS");
        trainRepository.save(train);

        // Seed a TrainPosition (simulating what LiveTrainService would persist)
        TrainPosition position = new TrainPosition();
        position.setTrain(train);
        position.setLatitude(22.7196);
        position.setLongitude(75.8577);
        position.setSpeed(85);
        position.setDelayMinutes(15);
        position.setCurrentStation("INDB");
        position.setNextStation("UJN");
        position.setPreviousStation("INDB");
        position.setTimestamp(LocalDateTime.now());
        position.setApiUpdateTimestamp(LocalDateTime.now().toString());
        trainPositionRepository.save(position);

        // Mock the ML service to return a realistic prediction
        MlPredictionResponse mockPrediction = MlPredictionResponse.builder()
                .trainNumber(TRAIN_NUMBER)
                .predictedRemainingMinutes(112.0)
                .confidenceScore(0.88)
                .modelVersion("hybrid-eta-v2-xgb-dual")
                .predictionSource("REAL_XGBOOST")
                .predictionTimestamp(LocalDateTime.now())
                .build();
        when(mlPredictionService.predict(any(FeatureRequest.class))).thenReturn(mockPrediction);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test 1: FeatureService builds a valid feature vector from DB state
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void featureService_buildsValidFeatureVector_fromPersistedPosition() {
        FeatureRequest features = featureService.buildFeaturesForTrain(TRAIN_NUMBER);

        assertThat(features).isNotNull();
        assertThat(features.getTrainNumber()).isEqualTo(TRAIN_NUMBER);
        assertThat(features.getCurrentSpeed()).isGreaterThanOrEqualTo(0.0);
        assertThat(features.getAverageSpeedLast5Minutes()).isGreaterThanOrEqualTo(0.0);
        assertThat(features.getCurrentDelayMinutes()).isEqualTo(15);
        assertThat(features.getLatitude()).isBetween(-90.0, 90.0);
        assertThat(features.getLongitude()).isBetween(-180.0, 180.0);
        assertThat(features.getStopsRemaining()).isGreaterThanOrEqualTo(0);
        assertThat(features.getDistanceRemaining()).isGreaterThanOrEqualTo(0.0);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test 2: Full pipeline — DB position → Features → ML → ETA saved to DB
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void etaService_generateAndSaveEta_fullPipeline_savesEtaToDB() {
        EtaResponse response = etaService.generateAndSaveEta(TRAIN_NUMBER);

        // Response is populated
        assertThat(response).isNotNull();
        assertThat(response.getTrainNumber()).isEqualTo(TRAIN_NUMBER);
        assertThat(response.getPredictedRemainingMinutes()).isEqualTo(112.0);
        assertThat(response.getConfidenceScore()).isEqualTo(0.88);
        assertThat(response.getModelVersion()).isEqualTo("hybrid-eta-v2-xgb-dual");
        assertThat(response.getPredictionSource()).isEqualTo("REAL_XGBOOST");

        // ETA timestamp is in the future (predicted ETA = now + 112 min)
        assertThat(response.getPredictedEta()).isAfter(LocalDateTime.now().minusMinutes(1));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test 3: ETA history — multiple predictions are persisted and retrievable
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void etaService_getEtaHistory_returnsMostRecentFirst() {
        // Trigger two prediction cycles
        etaService.generateAndSaveEta(TRAIN_NUMBER);
        etaService.generateAndSaveEta(TRAIN_NUMBER);

        var history = etaService.getEtaHistory(TRAIN_NUMBER);
        assertThat(history).hasSize(2);

        // Most recent should be first
        assertThat(history.get(0).getPredictionTimestamp())
                .isAfterOrEqualTo(history.get(1).getPredictionTimestamp());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test 4: getLatestEta returns the most recent stored prediction
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void etaService_getLatestEta_returnsLatestAfterMultiplePredictions() {
        etaService.generateAndSaveEta(TRAIN_NUMBER);
        etaService.generateAndSaveEta(TRAIN_NUMBER);

        EtaResponse latest = etaService.getLatestEta(TRAIN_NUMBER);
        assertThat(latest).isNotNull();
        assertThat(latest.getTrainNumber()).isEqualTo(TRAIN_NUMBER);
        assertThat(latest.getPredictedRemainingMinutes()).isEqualTo(112.0);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test 5: 5-minute rolling average speed is computed, not duplicated
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void featureService_averageSpeedLast5Minutes_computedFromHistory() {
        // Add a second position record at a different speed
        Train train = trainRepository.findById(TRAIN_NUMBER).orElseThrow();
        TrainPosition position2 = new TrainPosition();
        position2.setTrain(train);
        position2.setLatitude(22.7196);
        position2.setLongitude(75.8577);
        position2.setSpeed(45);  // slower speed reading
        position2.setDelayMinutes(15);
        position2.setCurrentStation("INDB");
        position2.setNextStation("UJN");
        position2.setPreviousStation("INDB");
        position2.setTimestamp(LocalDateTime.now().minusMinutes(3));
        position2.setApiUpdateTimestamp(LocalDateTime.now().toString());
        trainPositionRepository.save(position2);

        FeatureRequest features = featureService.buildFeaturesForTrain(TRAIN_NUMBER);

        // Average of speed=85 and speed=45 is 65 — not equal to either
        assertThat(features.getAverageSpeedLast5Minutes()).isNotEqualTo(features.getCurrentSpeed());
        assertThat(features.getAverageSpeedLast5Minutes()).isGreaterThan(0.0);
    }
}
