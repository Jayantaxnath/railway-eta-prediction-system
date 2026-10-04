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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EtaServiceTest {

    private FeatureService featureService;
    private MlPredictionService mlPredictionService;
    private EtaPredictionRepository etaPredictionRepository;
    private TrainRepository trainRepository;
    private EtaService etaService;

    @BeforeEach
    void setUp() {
        featureService = mock(FeatureService.class);
        mlPredictionService = mock(MlPredictionService.class);
        etaPredictionRepository = mock(EtaPredictionRepository.class);
        trainRepository = mock(TrainRepository.class);
        etaService = new EtaService(featureService, mlPredictionService, etaPredictionRepository, trainRepository);
    }

    @Test
    void testGenerateAndSaveEta_Success() {
        String trainNumber = "12919";
        LocalDateTime now = LocalDateTime.of(2026, 9, 4, 15, 0);

        FeatureRequest features = FeatureRequest.builder()
                .trainNumber(trainNumber)
                .currentDelayMinutes(15)
                .build();

        MlPredictionResponse mlResponse = MlPredictionResponse.builder()
                .trainNumber(trainNumber)
                .predictedRemainingMinutes(112.0) // 112 mins -> +1h 52m -> 16:52
                .confidenceScore(0.85)
                .modelVersion("v0.1-mock")
                .predictionSource("MOCK")
                .predictionTimestamp(now)
                .build();

        Train train = new Train(trainNumber, "Malwa Express", "NDLS", "INDB");

        when(featureService.buildFeaturesForTrain(trainNumber)).thenReturn(features);
        when(mlPredictionService.predict(features)).thenReturn(mlResponse);
        when(trainRepository.findById(trainNumber)).thenReturn(Optional.of(train));
        when(etaPredictionRepository.save(any(EtaPrediction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EtaResponse response = etaService.generateAndSaveEta(trainNumber);

        assertNotNull(response);
        assertEquals(trainNumber, response.getTrainNumber());
        assertEquals(15, response.getCurrentDelayMinutes());
        assertEquals(112.0, response.getPredictedRemainingMinutes());
        assertEquals(LocalDateTime.of(2026, 9, 4, 16, 52), response.getPredictedEta());
        assertEquals("MOCK", response.getPredictionSource());

        ArgumentCaptor<EtaPrediction> captor = ArgumentCaptor.forClass(EtaPrediction.class);
        verify(etaPredictionRepository, times(1)).save(captor.capture());
        assertEquals(LocalDateTime.of(2026, 9, 4, 16, 52), captor.getValue().getPredictedEta());
    }

    @Test
    void testGenerateAndSaveEta_MidnightCrossing() {
        String trainNumber = "12919";
        LocalDateTime now = LocalDateTime.of(2026, 9, 4, 23, 30); // 23:30

        FeatureRequest features = FeatureRequest.builder().trainNumber(trainNumber).currentDelayMinutes(10).build();
        MlPredictionResponse mlResponse = MlPredictionResponse.builder()
                .trainNumber(trainNumber)
                .predictedRemainingMinutes(60.0) // 60 mins -> 00:30 next day
                .predictionTimestamp(now)
                .build();

        when(featureService.buildFeaturesForTrain(trainNumber)).thenReturn(features);
        when(mlPredictionService.predict(features)).thenReturn(mlResponse);
        when(trainRepository.findById(trainNumber)).thenReturn(Optional.of(new Train(trainNumber, "Malwa Express", "NDLS", "INDB")));
        when(etaPredictionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EtaResponse response = etaService.generateAndSaveEta(trainNumber);

        assertEquals(LocalDateTime.of(2026, 9, 5, 0, 30), response.getPredictedEta());
    }

    @Test
    void testGenerateAndSaveEta_NegativePrediction_ThrowsEtaCalculationException() {
        String trainNumber = "12919";
        FeatureRequest features = FeatureRequest.builder().trainNumber(trainNumber).build();
        MlPredictionResponse mlResponse = MlPredictionResponse.builder()
                .trainNumber(trainNumber)
                .predictedRemainingMinutes(-10.0) // Invalid negative
                .predictionTimestamp(LocalDateTime.now())
                .build();

        when(featureService.buildFeaturesForTrain(trainNumber)).thenReturn(features);
        when(mlPredictionService.predict(features)).thenReturn(mlResponse);

        assertThrows(EtaCalculationException.class, () -> etaService.generateAndSaveEta(trainNumber));
    }

    @Test
    void testGetLatestEta_NotFound_ThrowsResourceNotFoundException() {
        when(etaPredictionRepository.findFirstByTrain_TrainNumberOrderByPredictionTimestampDesc("12919"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> etaService.getLatestEta("12919"));
    }
}
