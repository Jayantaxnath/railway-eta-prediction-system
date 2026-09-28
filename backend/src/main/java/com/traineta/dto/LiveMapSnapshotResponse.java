package com.traineta.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveMapSnapshotResponse {

    private Boolean success;
    private List<LiveMapTrain> data;

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public List<LiveMapTrain> getData() { return data; }
    public void setData(List<LiveMapTrain> data) { this.data = data; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LiveMapTrain {

        @JsonProperty("train_number")
        private String trainNumber;

        @JsonProperty("train_name")
        private String trainName;

        private String type;

        @JsonProperty("mins_since_dep")
        private Integer minsSinceDep;

        @JsonProperty("current_station")
        private String currentStation;

        @JsonProperty("current_station_name")
        private String currentStationName;

        @JsonProperty("current_lat")
        private Double currentLat;

        @JsonProperty("current_lng")
        private Double currentLng;

        @JsonProperty("next_station")
        private String nextStation;

        @JsonProperty("next_station_name")
        private String nextStationName;

        @JsonProperty("next_lat")
        private Double nextLat;

        @JsonProperty("next_lng")
        private Double nextLng;

        @JsonProperty("curr_distance")
        private Double currDistance;

        @JsonProperty("next_distance")
        private Double nextDistance;

        public String getTrainNumber() { return trainNumber; }
        public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

        public String getTrainName() { return trainName; }
        public void setTrainName(String trainName) { this.trainName = trainName; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public Integer getMinsSinceDep() { return minsSinceDep; }
        public void setMinsSinceDep(Integer minsSinceDep) { this.minsSinceDep = minsSinceDep; }

        public String getCurrentStation() { return currentStation; }
        public void setCurrentStation(String currentStation) { this.currentStation = currentStation; }

        public String getCurrentStationName() { return currentStationName; }
        public void setCurrentStationName(String currentStationName) { this.currentStationName = currentStationName; }

        public Double getCurrentLat() { return currentLat; }
        public void setCurrentLat(Double currentLat) { this.currentLat = currentLat; }

        public Double getCurrentLng() { return currentLng; }
        public void setCurrentLng(Double currentLng) { this.currentLng = currentLng; }

        public String getNextStation() { return nextStation; }
        public void setNextStation(String nextStation) { this.nextStation = nextStation; }

        public String getNextStationName() { return nextStationName; }
        public void setNextStationName(String nextStationName) { this.nextStationName = nextStationName; }

        public Double getNextLat() { return nextLat; }
        public void setNextLat(Double nextLat) { this.nextLat = nextLat; }

        public Double getNextLng() { return nextLng; }
        public void setNextLng(Double nextLng) { this.nextLng = nextLng; }

        public Double getCurrDistance() { return currDistance; }
        public void setCurrDistance(Double currDistance) { this.currDistance = currDistance; }

        public Double getNextDistance() { return nextDistance; }
        public void setNextDistance(Double nextDistance) { this.nextDistance = nextDistance; }
    }
}
