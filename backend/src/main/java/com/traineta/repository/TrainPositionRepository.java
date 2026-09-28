package com.traineta.repository;

import com.traineta.entity.TrainPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainPositionRepository extends JpaRepository<TrainPosition, Long> {
    Optional<TrainPosition> findFirstByTrain_TrainNumberOrderByTimestampDesc(String trainNumber);
    List<TrainPosition> findByTrain_TrainNumberOrderByTimestampDesc(String trainNumber);
    List<TrainPosition> findByTrain_TrainNumberAndTimestampGreaterThanEqualOrderByTimestampDesc(String trainNumber, LocalDateTime since);

    @Query("SELECT COUNT(DISTINCT p.train.trainNumber) FROM TrainPosition p WHERE (LOWER(p.currentStation) = LOWER(:stationCode) OR LOWER(p.nextStation) = LOWER(:stationCode)) AND p.timestamp >= :since")
    long countActiveTrainsNearStation(@Param("stationCode") String stationCode, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(DISTINCT p.train.trainNumber) FROM TrainPosition p WHERE (LOWER(p.currentStation) = LOWER(:stationCode) OR LOWER(p.nextStation) = LOWER(:stationCode)) AND p.timestamp >= :since AND p.delayMinutes > 15")
    long countDelayedTrainsNearStation(@Param("stationCode") String stationCode, @Param("since") LocalDateTime since);
}
