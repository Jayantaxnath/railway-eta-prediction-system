package com.traineta.repository;

import com.traineta.entity.EtaPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EtaPredictionRepository extends JpaRepository<EtaPrediction, Long> {
    Optional<EtaPrediction> findFirstByTrain_TrainNumberOrderByPredictionTimestampDesc(String trainNumber);
    List<EtaPrediction> findByTrain_TrainNumberOrderByPredictionTimestampDesc(String trainNumber);
}
