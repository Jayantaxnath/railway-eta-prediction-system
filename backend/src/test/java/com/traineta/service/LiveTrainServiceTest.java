package com.traineta.service;

import com.traineta.client.RailwayApiClient;
import com.traineta.dto.RailwayApiResponse;
import com.traineta.entity.Train;
import com.traineta.entity.TrainPosition;
import com.traineta.repository.TrainPositionRepository;
import com.traineta.repository.TrainRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LiveTrainServiceTest {

    private RailwayApiClient railwayApiClient;
    private TrainRepository trainRepository;
    private TrainPositionRepository trainPositionRepository;
    private LiveTrainService liveTrainService;

    @BeforeEach
    void setUp() {
        railwayApiClient = mock(RailwayApiClient.class);
        trainRepository = mock(TrainRepository.class);
        trainPositionRepository = mock(TrainPositionRepository.class);
        liveTrainService = new LiveTrainService(railwayApiClient, trainRepository, trainPositionRepository);
    }

    @Test
    void testFetchAndSaveLiveStatus_Success() {
        RailwayApiResponse.RailRadarData data = new RailwayApiResponse.RailRadarData();
        data.setTrainNumber("12919");
        data.setTrainName("Malwa SF Express");
        data.setDelayMinutes(54);
        data.setStartDate("2026-09-04");

        RailwayApiResponse.LocationInfo locationInfo = new RailwayApiResponse.LocationInfo("NSZ", "Nishatpura", 54, 286.1);
        data.setCurrentLocation(locationInfo);

        RailwayApiResponse.StationRef nextHalt = new RailwayApiResponse.StationRef("BHS", "Vidisha");
        data.setNextHalt(nextHalt);

        RailwayApiResponse response = new RailwayApiResponse(true, data);

        when(railwayApiClient.fetchLiveTrainStatus("12919")).thenReturn(Mono.just(response));
        when(trainRepository.findById("12919")).thenReturn(Optional.of(new Train("12919", "Malwa SF Express", "DADN", "SVDK")));

        liveTrainService.fetchAndSaveLiveStatus("12919");

        ArgumentCaptor<TrainPosition> captor = ArgumentCaptor.forClass(TrainPosition.class);
        verify(trainPositionRepository, times(1)).save(captor.capture());

        TrainPosition saved = captor.getValue();
        assertEquals("12919", saved.getTrain().getTrainNumber());
        assertEquals("NSZ", saved.getCurrentStation());
        assertEquals("BHS", saved.getNextStation());
        assertEquals(54, saved.getDelayMinutes());
    }
}
