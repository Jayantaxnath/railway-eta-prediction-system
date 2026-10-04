package com.traineta.service;

import com.traineta.dto.FeatureRequest;
import com.traineta.entity.Station;
import com.traineta.entity.Train;
import com.traineta.entity.TrainPosition;
import com.traineta.entity.TrainSchedule;
import com.traineta.exception.ResourceNotFoundException;
import com.traineta.repository.TrainPositionRepository;
import com.traineta.repository.TrainScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeatureServiceTest {

    private TrainPositionRepository trainPositionRepository;
    private TrainScheduleRepository trainScheduleRepository;
    private FeatureService featureService;

    @BeforeEach
    void setUp() {
        trainPositionRepository = mock(TrainPositionRepository.class);
        trainScheduleRepository = mock(TrainScheduleRepository.class);
        featureService = new FeatureService(trainPositionRepository, trainScheduleRepository);
    }

    @Test
    void testBuildFeaturesForTrain_ValidData() {
        Train train = new Train("12919", "Malwa Express", "NDLS", "INDB");
        LocalDateTime now = LocalDateTime.of(2026, 9, 4, 15, 30); // 15:30 -> hourOfDay 15, Friday (5)

        TrainPosition position = new TrainPosition();
        position.setTrain(train);
        position.setLatitude(23.123);
        position.setLongitude(75.456);
        position.setSpeed(65);
        position.setDelayMinutes(15);
        position.setCurrentStation("UJN");
        position.setTimestamp(now);

        Station st1 = new Station("NDLS", "New Delhi", 28.6139, 77.2090);
        Station st2 = new Station("UJN", "Ujjain", 23.1793, 75.7849);
        Station st3 = new Station("INDB", "Indore", 22.7196, 75.8577);

        List<TrainSchedule> schedules = List.of(
                new TrainSchedule(train, st1, "06:00", "06:10", 1),
                new TrainSchedule(train, st2, "14:00", "14:10", 1),
                new TrainSchedule(train, st3, "17:00", "17:00", 1)
        );

        when(trainPositionRepository.findFirstByTrain_TrainNumberOrderByTimestampDesc("12919"))
                .thenReturn(Optional.of(position));
        when(trainScheduleRepository.findByTrain_TrainNumberOrderByStopNumberAsc("12919"))
                .thenReturn(schedules);

        FeatureRequest features = featureService.buildFeaturesForTrain("12919");

        assertNotNull(features);
        assertEquals("12919", features.getTrainNumber());
        assertEquals(15, features.getCurrentDelayMinutes());
        assertEquals(65.0, features.getCurrentSpeed());
        assertEquals(23.123, features.getLatitude());
        assertEquals(75.456, features.getLongitude());
        assertEquals(1, features.getStopsRemaining()); // UJN (idx 1) -> 3 total - 1 - 1 = 1 stop remaining (INDB)
        assertEquals(15, features.getHourOfDay());
        assertEquals(5, features.getDayOfWeek()); // Friday = 5
        assertTrue(features.getDistanceRemaining() > 0.0);
    }

    @Test
    void testBuildFeaturesForTrain_MissingPosition_ThrowsNotFound() {
        when(trainPositionRepository.findFirstByTrain_TrainNumberOrderByTimestampDesc("12919"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> featureService.buildFeaturesForTrain("12919"));
    }

    @Test
    void testBuildFeaturesForTrain_InvalidSpeed_ThrowsIllegalArgument() {
        Train train = new Train("12919", "Malwa Express", "NDLS", "INDB");
        TrainPosition position = new TrainPosition();
        position.setTrain(train);
        position.setLatitude(23.123);
        position.setLongitude(75.456);
        position.setSpeed(-10); // invalid speed
        position.setDelayMinutes(5);
        position.setTimestamp(LocalDateTime.now());

        when(trainPositionRepository.findFirstByTrain_TrainNumberOrderByTimestampDesc("12919"))
                .thenReturn(Optional.of(position));

        assertThrows(IllegalArgumentException.class, () -> featureService.buildFeaturesForTrain("12919"));
    }

    @Test
    void testBuildFeaturesForTrain_InvalidLatitude_ThrowsIllegalArgument() {
        Train train = new Train("12919", "Malwa Express", "NDLS", "INDB");
        TrainPosition position = new TrainPosition();
        position.setTrain(train);
        position.setLatitude(120.0); // invalid lat > 90
        position.setLongitude(75.456);
        position.setSpeed(50);
        position.setDelayMinutes(5);

        when(trainPositionRepository.findFirstByTrain_TrainNumberOrderByTimestampDesc("12919"))
                .thenReturn(Optional.of(position));

        assertThrows(IllegalArgumentException.class, () -> featureService.buildFeaturesForTrain("12919"));
    }
}
