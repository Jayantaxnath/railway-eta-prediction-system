package com.traineta.service;

import com.traineta.client.WeatherApiClient;
import com.traineta.entity.Station;
import com.traineta.entity.WeatherData;
import com.traineta.repository.StationRepository;
import com.traineta.repository.WeatherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);

    private final WeatherApiClient weatherApiClient;
    private final WeatherRepository weatherRepository;
    private final StationRepository stationRepository;

    public WeatherService(WeatherApiClient weatherApiClient,
                          WeatherRepository weatherRepository,
                          StationRepository stationRepository) {
        this.weatherApiClient = weatherApiClient;
        this.weatherRepository = weatherRepository;
        this.stationRepository = stationRepository;
    }

    /**
     * Returns real-time rainfall in mm for a given station code using OpenWeatherMap API.
     */
    public Double getRainfallMm(String stationCode) {
        if (stationCode == null || stationCode.isBlank()) {
            return 0.0;
        }

        // 1. Check database cache (valid for 30 minutes)
        Optional<WeatherData> cached = weatherRepository.findFirstByStationCodeOrderByObservedAtDesc(stationCode);
        if (cached.isPresent()) {
            LocalDateTime observed = cached.get().getObservedAt();
            if (observed != null && observed.isAfter(LocalDateTime.now().minusMinutes(30))) {
                log.debug("Returning cached rainfall for station {}: {} mm", stationCode, cached.get().getRainfallMm());
                return cached.get().getRainfallMm();
            }
        }

        // 2. Fetch live data from OpenWeatherMap API
        Double rainfallMm = 0.0;
        Optional<Station> stationOpt = stationRepository.findById(stationCode);

        if (stationOpt.isPresent()) {
            Station station = stationOpt.get();
            if (station.getLatitude() != null && station.getLongitude() != null) {
                rainfallMm = weatherApiClient.fetchRainfallMmByCoordinates(station.getLatitude(), station.getLongitude());
            } else if (station.getStationName() != null) {
                rainfallMm = weatherApiClient.fetchRainfallMmByCity(station.getStationName());
            } else {
                rainfallMm = weatherApiClient.fetchRainfallMmByCity(stationCode);
            }
        } else {
            rainfallMm = weatherApiClient.fetchRainfallMmByCity(stationCode);
        }

        // 3. Save observation to database cache
        try {
            WeatherData newData = new WeatherData();
            newData.setStationCode(stationCode);
            newData.setRainfallMm(rainfallMm);
            newData.setObservedAt(LocalDateTime.now());
            weatherRepository.save(newData);
        } catch (Exception e) {
            log.warn("Failed to cache weather data for station {}: {}", stationCode, e.getMessage());
        }

        return rainfallMm;
    }
}
