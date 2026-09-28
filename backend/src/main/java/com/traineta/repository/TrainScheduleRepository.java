package com.traineta.repository;

import com.traineta.entity.TrainSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainScheduleRepository extends JpaRepository<TrainSchedule, Long> {
    /**
     * Returns a train's schedule ordered by stop sequence.
     * This guarantees correct stopsRemaining calculation in FeatureService.
     */
    List<TrainSchedule> findByTrain_TrainNumberOrderByStopNumberAsc(String trainNumber);
}
