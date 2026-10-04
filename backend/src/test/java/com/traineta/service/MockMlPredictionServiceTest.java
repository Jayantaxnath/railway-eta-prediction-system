package com.traineta.service;

import com.traineta.dto.FeatureRequest;
import com.traineta.dto.MlPredictionResponse;
import com.traineta.service.impl.MockMlPredictionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MockMlPredictionServiceTest {

    private MlPredictionService mlPredictionService;

    @BeforeEach
    void setUp() {
        mlPredictionService = new MockMlPredictionService();
    }

    @Test
    void testPredict_ValidFeatures_ReturnsMockPrediction() {
        FeatureRequest features = FeatureRequest.builder()
                .trainNumber("12919")
                .currentDelayMinutes(15)
                .currentSpeed(65.0)
                .latitude(23.123)
                .longitude(75.456)
                .scheduledRemainingMinutes(100L)
                .stopsRemaining(4)
                .hourOfDay(15)
                .dayOfWeek(5)
                .build();

        MlPredictionResponse response = mlPredictionService.predict(features);

        assertNotNull(response);
        assertEquals("12919", response.getTrainNumber());
        assertEquals(115.0, response.getPredictedRemainingMinutes()); // 100 + 15 = 115.0
        assertEquals("MOCK", response.getPredictionSource());
        assertEquals("v0.1-mock", response.getModelVersion());
        assertNotNull(response.getPredictionTimestamp());
    }

    @Test
    void testPredict_NullFeatures_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> mlPredictionService.predict(null));
    }
}
