package com.traineta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PythonMlRequest {

    @JsonProperty("train_id")
    private String trainId;

    @JsonProperty("target_station_id")
    private String targetStationId;

    @JsonProperty("observed_at")
    private String observedAt;

    @JsonProperty("current_speed_kmph")
    private Double currentSpeedKmph;

    @JsonProperty("average_speed_last_5_minutes")
    private Double averageSpeedLast5Minutes;

    @JsonProperty("current_delay_minutes")
    private Double currentDelayMinutes;

    @JsonProperty("distance_to_target_km")
    private Double distanceToTargetKm;

    @JsonProperty("scheduled_time_to_target_minutes")
    private Double scheduledTimeToTargetMinutes;

    @JsonProperty("rainfall_mm")
    private Double rainfallMm;

    @JsonProperty("congestion_score")
    private Double congestionScore;

    @JsonProperty("historical_section_average_minutes")
    private Double historicalSectionAverageMinutes;

    public PythonMlRequest() {}

    public PythonMlRequest(String trainId, String targetStationId, String observedAt, Double currentSpeedKmph, Double averageSpeedLast5Minutes, Double currentDelayMinutes, Double distanceToTargetKm, Double scheduledTimeToTargetMinutes, Double rainfallMm, Double congestionScore, Double historicalSectionAverageMinutes) {
        this.trainId = trainId;
        this.targetStationId = targetStationId;
        this.observedAt = observedAt;
        this.currentSpeedKmph = currentSpeedKmph;
        this.averageSpeedLast5Minutes = averageSpeedLast5Minutes;
        this.currentDelayMinutes = currentDelayMinutes;
        this.distanceToTargetKm = distanceToTargetKm;
        this.scheduledTimeToTargetMinutes = scheduledTimeToTargetMinutes;
        this.rainfallMm = rainfallMm;
        this.congestionScore = congestionScore;
        this.historicalSectionAverageMinutes = historicalSectionAverageMinutes;
    }

    public String getTrainId() { return trainId; }
    public void setTrainId(String trainId) { this.trainId = trainId; }

    public String getTargetStationId() { return targetStationId; }
    public void setTargetStationId(String targetStationId) { this.targetStationId = targetStationId; }

    public String getObservedAt() { return observedAt; }
    public void setObservedAt(String observedAt) { this.observedAt = observedAt; }

    public Double getCurrentSpeedKmph() { return currentSpeedKmph; }
    public void setCurrentSpeedKmph(Double currentSpeedKmph) { this.currentSpeedKmph = currentSpeedKmph; }

    public Double getAverageSpeedLast5Minutes() { return averageSpeedLast5Minutes; }
    public void setAverageSpeedLast5Minutes(Double averageSpeedLast5Minutes) { this.averageSpeedLast5Minutes = averageSpeedLast5Minutes; }

    public Double getCurrentDelayMinutes() { return currentDelayMinutes; }
    public void setCurrentDelayMinutes(Double currentDelayMinutes) { this.currentDelayMinutes = currentDelayMinutes; }

    public Double getDistanceToTargetKm() { return distanceToTargetKm; }
    public void setDistanceToTargetKm(Double distanceToTargetKm) { this.distanceToTargetKm = distanceToTargetKm; }

    public Double getScheduledTimeToTargetMinutes() { return scheduledTimeToTargetMinutes; }
    public void setScheduledTimeToTargetMinutes(Double scheduledTimeToTargetMinutes) { this.scheduledTimeToTargetMinutes = scheduledTimeToTargetMinutes; }

    public Double getRainfallMm() { return rainfallMm; }
    public void setRainfallMm(Double rainfallMm) { this.rainfallMm = rainfallMm; }

    public Double getCongestionScore() { return congestionScore; }
    public void setCongestionScore(Double congestionScore) { this.congestionScore = congestionScore; }

    public Double getHistoricalSectionAverageMinutes() { return historicalSectionAverageMinutes; }
    public void setHistoricalSectionAverageMinutes(Double historicalSectionAverageMinutes) { this.historicalSectionAverageMinutes = historicalSectionAverageMinutes; }

    public static PythonMlRequestBuilder builder() { return new PythonMlRequestBuilder(); }

    public static class PythonMlRequestBuilder {
        private String trainId;
        private String targetStationId;
        private String observedAt;
        private Double currentSpeedKmph;
        private Double averageSpeedLast5Minutes;
        private Double currentDelayMinutes;
        private Double distanceToTargetKm;
        private Double scheduledTimeToTargetMinutes;
        private Double rainfallMm;
        private Double congestionScore;
        private Double historicalSectionAverageMinutes;

        public PythonMlRequestBuilder trainId(String trainId) { this.trainId = trainId; return this; }
        public PythonMlRequestBuilder targetStationId(String targetStationId) { this.targetStationId = targetStationId; return this; }
        public PythonMlRequestBuilder observedAt(String observedAt) { this.observedAt = observedAt; return this; }
        public PythonMlRequestBuilder currentSpeedKmph(Double currentSpeedKmph) { this.currentSpeedKmph = currentSpeedKmph; return this; }
        public PythonMlRequestBuilder averageSpeedLast5Minutes(Double averageSpeedLast5Minutes) { this.averageSpeedLast5Minutes = averageSpeedLast5Minutes; return this; }
        public PythonMlRequestBuilder currentDelayMinutes(Double currentDelayMinutes) { this.currentDelayMinutes = currentDelayMinutes; return this; }
        public PythonMlRequestBuilder distanceToTargetKm(Double distanceToTargetKm) { this.distanceToTargetKm = distanceToTargetKm; return this; }
        public PythonMlRequestBuilder scheduledTimeToTargetMinutes(Double scheduledTimeToTargetMinutes) { this.scheduledTimeToTargetMinutes = scheduledTimeToTargetMinutes; return this; }
        public PythonMlRequestBuilder rainfallMm(Double rainfallMm) { this.rainfallMm = rainfallMm; return this; }
        public PythonMlRequestBuilder congestionScore(Double congestionScore) { this.congestionScore = congestionScore; return this; }
        public PythonMlRequestBuilder historicalSectionAverageMinutes(Double historicalSectionAverageMinutes) { this.historicalSectionAverageMinutes = historicalSectionAverageMinutes; return this; }

        public PythonMlRequest build() {
            return new PythonMlRequest(trainId, targetStationId, observedAt, currentSpeedKmph, averageSpeedLast5Minutes, currentDelayMinutes, distanceToTargetKm, scheduledTimeToTargetMinutes, rainfallMm, congestionScore, historicalSectionAverageMinutes);
        }
    }
}

