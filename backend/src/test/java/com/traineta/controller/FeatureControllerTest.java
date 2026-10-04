package com.traineta.controller;

import com.traineta.dto.FeatureRequest;
import com.traineta.service.FeatureService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeatureController.class)
class FeatureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeatureService featureService;

    @Test
    void testGetTrainFeatures_Success() throws Exception {
        FeatureRequest mockFeatures = FeatureRequest.builder()
                .trainNumber("12919")
                .currentDelayMinutes(15)
                .currentSpeed(65.0)
                .latitude(23.123)
                .longitude(75.456)
                .distanceRemaining(120.5)
                .scheduledRemainingMinutes(100L)
                .stopsRemaining(4)
                .hourOfDay(15)
                .dayOfWeek(5)
                .build();

        when(featureService.buildFeaturesForTrain("12919")).thenReturn(mockFeatures);

        mockMvc.perform(get("/api/trains/12919/features"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainNumber").value("12919"))
                .andExpect(jsonPath("$.currentDelayMinutes").value(15))
                .andExpect(jsonPath("$.currentSpeed").value(65.0))
                .andExpect(jsonPath("$.stopsRemaining").value(4))
                .andExpect(jsonPath("$.hourOfDay").value(15));
    }
}
