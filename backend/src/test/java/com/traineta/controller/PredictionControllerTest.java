package com.traineta.controller;

import com.traineta.dto.FeatureRequest;
import com.traineta.dto.MlPredictionResponse;
import com.traineta.service.FeatureService;
import com.traineta.service.MlPredictionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PredictionController.class)
class PredictionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeatureService featureService;

    @MockBean
    private MlPredictionService mlPredictionService;

    @Test
    void testGetPrediction_Success() throws Exception {
        FeatureRequest mockFeatures = FeatureRequest.builder()
                .trainNumber("12919")
                .currentDelayMinutes(15)
                .currentSpeed(65.0)
                .latitude(23.123)
                .longitude(75.456)
                .scheduledRemainingMinutes(100L)
                .build();

        MlPredictionResponse mockResponse = MlPredictionResponse.builder()
                .trainNumber("12919")
                .predictedRemainingMinutes(115.0)
                .confidenceScore(0.85)
                .modelVersion("v0.1-mock")
                .predictionSource("MOCK")
                .predictionTimestamp(LocalDateTime.now())
                .build();

        when(featureService.buildFeaturesForTrain("12919")).thenReturn(mockFeatures);
        when(mlPredictionService.predict(any(FeatureRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(get("/api/trains/12919/prediction"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainNumber").value("12919"))
                .andExpect(jsonPath("$.predictedRemainingMinutes").value(115.0))
                .andExpect(jsonPath("$.predictionSource").value("MOCK"))
                .andExpect(jsonPath("$.modelVersion").value("v0.1-mock"));
    }
}
