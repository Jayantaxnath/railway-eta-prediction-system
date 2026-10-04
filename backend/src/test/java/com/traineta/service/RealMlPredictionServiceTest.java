package com.traineta.service;

import com.traineta.client.MlApiClient;
import com.traineta.dto.FeatureRequest;
import com.traineta.dto.MlPredictionResponse;
import com.traineta.dto.PythonMlResponse;
import com.traineta.service.impl.RealMlPredictionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RealMlPredictionServiceTest {

    private MlApiClient mlApiClient;
    private RealMlPredictionService realMlPredictionService;

    @BeforeEach
    void setUp() {
        mlApiClient = mock(MlApiClient.class);
        realMlPredictionService = new RealMlPredictionService(mlApiClient);
    }

    @Test
    void testPredict_SuccessFromPythonApi() {
        FeatureRequest features = FeatureRequest.builder()
                .trainNumber("12919")
                .currentDelayMinutes(15)
                .currentSpeed(65.0)
                .distanceRemaining(120.0)
                .scheduledRemainingMinutes(110L)
                .build();

        PythonMlResponse pythonResponse = new PythonMlResponse(
                "12919", "DEST", 135.0, 115.0, 155.0, "hybrid-eta-v1"
        );

        when(mlApiClient.predictEta(any())).thenReturn(Mono.just(pythonResponse));

        MlPredictionResponse response = realMlPredictionService.predict(features);

        assertNotNull(response);
        assertEquals("12919", response.getTrainNumber());
        assertEquals(135.0, response.getPredictedRemainingMinutes());
        assertEquals("REAL_XGBOOST", response.getPredictionSource());
        assertEquals("hybrid-eta-v1", response.getModelVersion());
    }

    @Test
    void testPredict_FallbackWhenPythonApiNull() {
        FeatureRequest features = FeatureRequest.builder()
                .trainNumber("12919")
                .currentDelayMinutes(15)
                .currentSpeed(65.0)
                .distanceRemaining(120.0)
                .scheduledRemainingMinutes(110L)
                .build();

        when(mlApiClient.predictEta(any())).thenReturn(Mono.empty());

        MlPredictionResponse response = realMlPredictionService.predict(features);

        assertNotNull(response);
        assertEquals("12919", response.getTrainNumber());
        assertEquals(125.0, response.getPredictedRemainingMinutes()); // 110 scheduled + 15 delay
        assertEquals("FALLBACK_MOCK", response.getPredictionSource());
        assertEquals("v0.1-fallback", response.getModelVersion());
    }

    @Test
    void testPredict_NullPayload_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> realMlPredictionService.predict(null));
    }
}
