package com.traineta.repository;

import com.traineta.entity.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherData, Long> {
    Optional<WeatherData> findFirstByStationCodeOrderByObservedAtDesc(String stationCode);
}
