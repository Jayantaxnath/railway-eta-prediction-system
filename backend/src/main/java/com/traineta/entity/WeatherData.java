package com.traineta.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_data")
public class WeatherData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stationCode;
    private Double rainfallMm;
    private Double temperatureCelsius;
    private LocalDateTime observedAt;

    public WeatherData() {}

    public WeatherData(Long id, String stationCode, Double rainfallMm, Double temperatureCelsius, LocalDateTime observedAt) {
        this.id = id;
        this.stationCode = stationCode;
        this.rainfallMm = rainfallMm;
        this.temperatureCelsius = temperatureCelsius;
        this.observedAt = observedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStationCode() { return stationCode; }
    public void setStationCode(String stationCode) { this.stationCode = stationCode; }

    public Double getRainfallMm() { return rainfallMm; }
    public void setRainfallMm(Double rainfallMm) { this.rainfallMm = rainfallMm; }

    public Double getTemperatureCelsius() { return temperatureCelsius; }
    public void setTemperatureCelsius(Double temperatureCelsius) { this.temperatureCelsius = temperatureCelsius; }

    public LocalDateTime getObservedAt() { return observedAt; }
    public void setObservedAt(LocalDateTime observedAt) { this.observedAt = observedAt; }
}

