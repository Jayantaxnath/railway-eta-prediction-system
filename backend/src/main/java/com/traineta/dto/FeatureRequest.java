package com.traineta.dto;

public class FeatureRequest {

    private String trainNumber;
    private String targetStationId;
    private Integer currentDelayMinutes;
    private Double currentSpeed;
    private Double averageSpeedLast5Minutes;
    private Double latitude;
    private Double longitude;
    private Double distanceRemaining;
    private Long scheduledRemainingMinutes;
    private Integer stopsRemaining;
    private Integer hourOfDay;
    private Integer dayOfWeek;
    private Double rainfallMm;
    private Double congestionScore;

    public FeatureRequest() {}

    public FeatureRequest(String trainNumber, String targetStationId, Integer currentDelayMinutes, Double currentSpeed, Double averageSpeedLast5Minutes, Double latitude, Double longitude, Double distanceRemaining, Long scheduledRemainingMinutes, Integer stopsRemaining, Integer hourOfDay, Integer dayOfWeek, Double rainfallMm, Double congestionScore) {
        this.trainNumber = trainNumber;
        this.targetStationId = targetStationId;
        this.currentDelayMinutes = currentDelayMinutes;
        this.currentSpeed = currentSpeed;
        this.averageSpeedLast5Minutes = averageSpeedLast5Minutes;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceRemaining = distanceRemaining;
        this.scheduledRemainingMinutes = scheduledRemainingMinutes;
        this.stopsRemaining = stopsRemaining;
        this.hourOfDay = hourOfDay;
        this.dayOfWeek = dayOfWeek;
        this.rainfallMm = rainfallMm;
        this.congestionScore = congestionScore;
    }

    public FeatureRequest(String trainNumber, Integer currentDelayMinutes, Double currentSpeed, Double averageSpeedLast5Minutes, Double latitude, Double longitude, Double distanceRemaining, Long scheduledRemainingMinutes, Integer stopsRemaining, Integer hourOfDay, Integer dayOfWeek, Double rainfallMm, Double congestionScore) {
        this(trainNumber, "DEST", currentDelayMinutes, currentSpeed, averageSpeedLast5Minutes, latitude, longitude, distanceRemaining, scheduledRemainingMinutes, stopsRemaining, hourOfDay, dayOfWeek, rainfallMm, congestionScore);
    }

    public FeatureRequest(String trainNumber, Integer currentDelayMinutes, Double currentSpeed, Double latitude, Double longitude, Double distanceRemaining, Long scheduledRemainingMinutes, Integer stopsRemaining, Integer hourOfDay, Integer dayOfWeek, Double rainfallMm, Double congestionScore) {
        this(trainNumber, "DEST", currentDelayMinutes, currentSpeed, currentSpeed, latitude, longitude, distanceRemaining, scheduledRemainingMinutes, stopsRemaining, hourOfDay, dayOfWeek, rainfallMm, congestionScore);
    }

    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

    public String getTargetStationId() { return targetStationId; }
    public void setTargetStationId(String targetStationId) { this.targetStationId = targetStationId; }

    public Integer getCurrentDelayMinutes() { return currentDelayMinutes; }
    public void setCurrentDelayMinutes(Integer currentDelayMinutes) { this.currentDelayMinutes = currentDelayMinutes; }

    public Double getCurrentSpeed() { return currentSpeed; }
    public void setCurrentSpeed(Double currentSpeed) { this.currentSpeed = currentSpeed; }

    public Double getAverageSpeedLast5Minutes() { return averageSpeedLast5Minutes; }
    public void setAverageSpeedLast5Minutes(Double averageSpeedLast5Minutes) { this.averageSpeedLast5Minutes = averageSpeedLast5Minutes; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getDistanceRemaining() { return distanceRemaining; }
    public void setDistanceRemaining(Double distanceRemaining) { this.distanceRemaining = distanceRemaining; }

    public Long getScheduledRemainingMinutes() { return scheduledRemainingMinutes; }
    public void setScheduledRemainingMinutes(Long scheduledRemainingMinutes) { this.scheduledRemainingMinutes = scheduledRemainingMinutes; }

    public Integer getStopsRemaining() { return stopsRemaining; }
    public void setStopsRemaining(Integer stopsRemaining) { this.stopsRemaining = stopsRemaining; }

    public Integer getHourOfDay() { return hourOfDay; }
    public void setHourOfDay(Integer hourOfDay) { this.hourOfDay = hourOfDay; }

    public Integer getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public Double getRainfallMm() { return rainfallMm; }
    public void setRainfallMm(Double rainfallMm) { this.rainfallMm = rainfallMm; }

    public Double getCongestionScore() { return congestionScore; }
    public void setCongestionScore(Double congestionScore) { this.congestionScore = congestionScore; }

    public static FeatureRequestBuilder builder() { return new FeatureRequestBuilder(); }

    public static class FeatureRequestBuilder {
        private String trainNumber;
        private String targetStationId;
        private Integer currentDelayMinutes;
        private Double currentSpeed;
        private Double averageSpeedLast5Minutes;
        private Double latitude;
        private Double longitude;
        private Double distanceRemaining;
        private Long scheduledRemainingMinutes;
        private Integer stopsRemaining;
        private Integer hourOfDay;
        private Integer dayOfWeek;
        private Double rainfallMm;
        private Double congestionScore;

        public FeatureRequestBuilder trainNumber(String trainNumber) { this.trainNumber = trainNumber; return this; }
        public FeatureRequestBuilder targetStationId(String targetStationId) { this.targetStationId = targetStationId; return this; }
        public FeatureRequestBuilder currentDelayMinutes(Integer currentDelayMinutes) { this.currentDelayMinutes = currentDelayMinutes; return this; }
        public FeatureRequestBuilder currentSpeed(Double currentSpeed) { this.currentSpeed = currentSpeed; return this; }
        public FeatureRequestBuilder averageSpeedLast5Minutes(Double averageSpeedLast5Minutes) { this.averageSpeedLast5Minutes = averageSpeedLast5Minutes; return this; }
        public FeatureRequestBuilder latitude(Double latitude) { this.latitude = latitude; return this; }
        public FeatureRequestBuilder longitude(Double longitude) { this.longitude = longitude; return this; }
        public FeatureRequestBuilder distanceRemaining(Double distanceRemaining) { this.distanceRemaining = distanceRemaining; return this; }
        public FeatureRequestBuilder scheduledRemainingMinutes(Long scheduledRemainingMinutes) { this.scheduledRemainingMinutes = scheduledRemainingMinutes; return this; }
        public FeatureRequestBuilder stopsRemaining(Integer stopsRemaining) { this.stopsRemaining = stopsRemaining; return this; }
        public FeatureRequestBuilder hourOfDay(Integer hourOfDay) { this.hourOfDay = hourOfDay; return this; }
        public FeatureRequestBuilder dayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; return this; }
        public FeatureRequestBuilder rainfallMm(Double rainfallMm) { this.rainfallMm = rainfallMm; return this; }
        public FeatureRequestBuilder congestionScore(Double congestionScore) { this.congestionScore = congestionScore; return this; }

        public FeatureRequest build() {
            return new FeatureRequest(trainNumber, targetStationId, currentDelayMinutes, currentSpeed, averageSpeedLast5Minutes, latitude, longitude, distanceRemaining, scheduledRemainingMinutes, stopsRemaining, hourOfDay, dayOfWeek, rainfallMm, congestionScore);
        }
    }
}

