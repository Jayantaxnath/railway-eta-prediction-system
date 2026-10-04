package com.traineta.controller;

import com.traineta.dto.EtaResponse;
import com.traineta.service.EtaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EtaController.class)
class EtaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EtaService etaService;

    @Test
    void testGetLatestEta_Success() throws Exception {
        EtaResponse mockResponse = EtaResponse.builder()
                .trainNumber("12919")
                .currentDelayMinutes(15)
                .predictedRemainingMinutes(112.0)
                .predictedEta(LocalDateTime.of(2026, 9, 4, 16, 52))
                .confidenceScore(0.85)
                .predictionSource("MOCK")
                .modelVersion("v0.1-mock")
                .predictionTimestamp(LocalDateTime.of(2026, 9, 4, 15, 0))
                .build();

        when(etaService.getLatestEta("12919")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/trains/12919/eta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainNumber").value("12919"))
                .andExpect(jsonPath("$.currentDelayMinutes").value(15))
                .andExpect(jsonPath("$.predictedRemainingMinutes").value(112.0))
                .andExpect(jsonPath("$.predictionSource").value("MOCK"));
    }

    @Test
    void testTriggerEtaPrediction_Success() throws Exception {
        EtaResponse mockResponse = EtaResponse.builder()
                .trainNumber("12919")
                .currentDelayMinutes(15)
                .predictedRemainingMinutes(112.0)
                .predictedEta(LocalDateTime.of(2026, 9, 4, 16, 52))
                .predictionSource("MOCK")
                .build();

        when(etaService.generateAndSaveEta("12919")).thenReturn(mockResponse);

        mockMvc.perform(post("/api/trains/12919/eta/predict"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainNumber").value("12919"))
                .andExpect(jsonPath("$.predictedRemainingMinutes").value(112.0));
    }
}
