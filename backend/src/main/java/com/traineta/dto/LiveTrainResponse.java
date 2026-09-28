package com.traineta.dto;

public class LiveTrainResponse {

    private String trainNumber;
    private String currentStation;
    private String nextStation;
    private Double latitude;
    private Double longitude;
    private Integer speed;
    private Integer delayMinutes;
    private String lastUpdated;

    public LiveTrainResponse() {}

    public LiveTrainResponse(String trainNumber, String currentStation, String nextStation, Double latitude, Double longitude, Integer speed, Integer delayMinutes, String lastUpdated) {
        this.trainNumber = trainNumber;
        this.currentStation = currentStation;
        this.nextStation = nextStation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.delayMinutes = delayMinutes;
        this.lastUpdated = lastUpdated;
    }

    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

    public String getCurrentStation() { return currentStation; }
    public void setCurrentStation(String currentStation) { this.currentStation = currentStation; }

    public String getNextStation() { return nextStation; }
    public void setNextStation(String nextStation) { this.nextStation = nextStation; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Integer getSpeed() { return speed; }
    public void setSpeed(Integer speed) { this.speed = speed; }

    public Integer getDelayMinutes() { return delayMinutes; }
    public void setDelayMinutes(Integer delayMinutes) { this.delayMinutes = delayMinutes; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
}

