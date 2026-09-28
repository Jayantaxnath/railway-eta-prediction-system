package com.traineta.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "stations")
public class Station {

    @Id
    private String stationCode;
    private String stationName;
    private Double latitude;
    private Double longitude;

    public Station() {}

    public Station(String stationCode, String stationName, Double latitude, Double longitude) {
        this.stationCode = stationCode;
        this.stationName = stationName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getStationCode() { return stationCode; }
    public void setStationCode(String stationCode) { this.stationCode = stationCode; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}

