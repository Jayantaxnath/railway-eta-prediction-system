package com.traineta.scheduler;

import com.traineta.service.EtaService;
import com.traineta.service.LiveTrainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

class TrainDataSchedulerTest {

    private LiveTrainService liveTrainService;
    private EtaService etaService;
    private TrainDataScheduler trainDataScheduler;

    @BeforeEach
    void setUp() {
        liveTrainService = mock(LiveTrainService.class);
        etaService = mock(EtaService.class);
        trainDataScheduler = new TrainDataScheduler(liveTrainService, etaService);
        ReflectionTestUtils.setField(trainDataScheduler, "activeTrainNumbersStr", "12919, 12920");
    }

    @Test
    void testFetchActiveTrainPositions_TriggersIngestionAndEta() {
        trainDataScheduler.fetchActiveTrainPositions();

        verify(liveTrainService, times(1)).fetchAndSaveLiveStatus("12919");
        verify(liveTrainService, times(1)).fetchAndSaveLiveStatus("12920");

        verify(etaService, times(1)).generateAndSaveEta("12919");
        verify(etaService, times(1)).generateAndSaveEta("12920");
    }
}
