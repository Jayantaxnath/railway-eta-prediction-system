package com.traineta.controller;

import com.traineta.dto.LiveTrainResponse;
import com.traineta.dto.TrainResponse;
import com.traineta.service.TrainService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainController.class)
class TrainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainService trainService;

    @Test
    void testGetAllTrains() throws Exception {
        when(trainService.getAllTrains()).thenReturn(List.of(
                new TrainResponse("12919", "Malwa Express", "NDLS", "INDB")
        ));

        mockMvc.perform(get("/api/trains"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainNumber").value("12919"))
                .andExpect(jsonPath("$[0].trainName").value("Malwa Express"));
    }

    @Test
    void testGetLatestLivePosition() throws Exception {
        when(trainService.getLatestLivePosition("12919")).thenReturn(
                new LiveTrainResponse("12919", "UJN", "INDB", 23.123, 75.456, 65, 10, "2026-09-04T19:30:00")
        );

        mockMvc.perform(get("/api/trains/12919/live"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainNumber").value("12919"))
                .andExpect(jsonPath("$.currentStation").value("UJN"))
                .andExpect(jsonPath("$.speed").value(65));
    }
}
