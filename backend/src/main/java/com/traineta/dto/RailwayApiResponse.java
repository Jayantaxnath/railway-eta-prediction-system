package com.traineta.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RailwayApiResponse {

    private Boolean success;
    private RailRadarData data;

    public RailwayApiResponse() {}

    public RailwayApiResponse(Boolean success, RailRadarData data) {
        this.success = success;
        this.data = data;
    }

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public RailRadarData getData() { return data; }
    public void setData(RailRadarData data) { this.data = data; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RailRadarData {
        private String trainNumber;
        private String trainName;
        private String startDate;
        private String lastUpdatedAt;
        private String status;
        private Integer delayMinutes;
        private Boolean isLive;

        private TrainInfo train;
        private LocationInfo currentLocation;
        private StationRef previousHalt;
        private StationRef nextHalt;
        private List<LiveRouteStation> route;

        public RailRadarData() {}

        public String getTrainNumber() { return trainNumber; }
        public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

        public String getTrainName() { return trainName; }
        public void setTrainName(String trainName) { this.trainName = trainName; }

        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }

        public String getLastUpdatedAt() { return lastUpdatedAt; }
        public void setLastUpdatedAt(String lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Integer getDelayMinutes() { return delayMinutes; }
        public void setDelayMinutes(Integer delayMinutes) { this.delayMinutes = delayMinutes; }

        public Boolean getIsLive() { return isLive; }
        public void setIsLive(Boolean isLive) { this.isLive = isLive; }

        public TrainInfo getTrain() { return train; }
        public void setTrain(TrainInfo train) { this.train = train; }

        public LocationInfo getCurrentLocation() { return currentLocation; }
        public void setCurrentLocation(LocationInfo currentLocation) { this.currentLocation = currentLocation; }

        public StationRef getPreviousHalt() { return previousHalt; }
        public void setPreviousHalt(StationRef previousHalt) { this.previousHalt = previousHalt; }

        public StationRef getNextHalt() { return nextHalt; }
        public void setNextHalt(StationRef nextHalt) { this.nextHalt = nextHalt; }

        public List<LiveRouteStation> getRoute() { return route; }
        public void setRoute(List<LiveRouteStation> route) { this.route = route; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TrainInfo {
        private String number;
        private String name;
        private String type;
        private String category;
        private StationDetail source;
        private StationDetail destination;
        private List<String> runDays;
        private Integer distance;
        private Integer duration;
        private Double avgSpeed;
        private Double maxSpeed;
        private Integer totalHalts;
        private String returnTrain;
        private String coachPosition;

        public TrainInfo() {}

        public String getNumber() { return number; }
        public void setNumber(String number) { this.number = number; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public StationDetail getSource() { return source; }
        public void setSource(StationDetail source) { this.source = source; }

        public StationDetail getDestination() { return destination; }
        public void setDestination(StationDetail destination) { this.destination = destination; }

        public List<String> getRunDays() { return runDays; }
        public void setRunDays(List<String> runDays) { this.runDays = runDays; }

        public Integer getDistance() { return distance; }
        public void setDistance(Integer distance) { this.distance = distance; }

        public Integer getDuration() { return duration; }
        public void setDuration(Integer duration) { this.duration = duration; }

        public Double getAvgSpeed() { return avgSpeed; }
        public void setAvgSpeed(Double avgSpeed) { this.avgSpeed = avgSpeed; }

        public Double getMaxSpeed() { return maxSpeed; }
        public void setMaxSpeed(Double maxSpeed) { this.maxSpeed = maxSpeed; }

        public Integer getTotalHalts() { return totalHalts; }
        public void setTotalHalts(Integer totalHalts) { this.totalHalts = totalHalts; }

        public String getReturnTrain() { return returnTrain; }
        public void setReturnTrain(String returnTrain) { this.returnTrain = returnTrain; }

        public String getCoachPosition() { return coachPosition; }
        public void setCoachPosition(String coachPosition) { this.coachPosition = coachPosition; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StationDetail {
        private String code;
        private String name;
        private Double lat;
        private Double lng;

        public StationDetail() {}

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Double getLat() { return lat; }
        public void setLat(Double lat) { this.lat = lat; }

        public Double getLng() { return lng; }
        public void setLng(Double lng) { this.lng = lng; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocationInfo {
        private String stationCode;
        private String stationName;
        private Integer sequence;
        private String status;
        private Boolean isHalt;
        private Boolean isDiverted;
        private Boolean isActualPosition;
        private Double segmentProgress;
        private Double speedKmh;
        private Integer bearingDegrees;
        private Integer delayMinutes;
        private Double distanceFromOriginKm;

        public LocationInfo() {}

        public LocationInfo(String stationCode, String stationName, Integer sequence, Double distanceFromOriginKm) {
            this.stationCode = stationCode;
            this.stationName = stationName;
            this.sequence = sequence;
            this.distanceFromOriginKm = distanceFromOriginKm;
        }

        public String getStationCode() { return stationCode; }
        public void setStationCode(String stationCode) { this.stationCode = stationCode; }

        public String getStationName() { return stationName; }
        public void setStationName(String stationName) { this.stationName = stationName; }

        public Integer getSequence() { return sequence; }
        public void setSequence(Integer sequence) { this.sequence = sequence; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Boolean getIsHalt() { return isHalt; }
        public void setIsHalt(Boolean isHalt) { this.isHalt = isHalt; }

        public Boolean getIsDiverted() { return isDiverted; }
        public void setIsDiverted(Boolean isDiverted) { this.isDiverted = isDiverted; }

        public Boolean getIsActualPosition() { return isActualPosition; }
        public void setIsActualPosition(Boolean isActualPosition) { this.isActualPosition = isActualPosition; }

        public Double getSegmentProgress() { return segmentProgress; }
        public void setSegmentProgress(Double segmentProgress) { this.segmentProgress = segmentProgress; }

        public Double getSpeedKmh() { return speedKmh; }
        public void setSpeedKmh(Double speedKmh) { this.speedKmh = speedKmh; }

        public Integer getBearingDegrees() { return bearingDegrees; }
        public void setBearingDegrees(Integer bearingDegrees) { this.bearingDegrees = bearingDegrees; }

        public Integer getDelayMinutes() { return delayMinutes; }
        public void setDelayMinutes(Integer delayMinutes) { this.delayMinutes = delayMinutes; }

        public Double getDistanceFromOriginKm() { return distanceFromOriginKm; }
        public void setDistanceFromOriginKm(Double distanceFromOriginKm) { this.distanceFromOriginKm = distanceFromOriginKm; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StationRef {
        private String stationCode;
        private String stationName;
        private Integer sequence;
        private Double distance;

        public StationRef() {}

        public StationRef(String stationCode, String stationName) {
            this.stationCode = stationCode;
            this.stationName = stationName;
        }

        public String getStationCode() { return stationCode; }
        public void setStationCode(String stationCode) { this.stationCode = stationCode; }

        public String getStationName() { return stationName; }
        public void setStationName(String stationName) { this.stationName = stationName; }

        public Integer getSequence() { return sequence; }
        public void setSequence(Integer sequence) { this.sequence = sequence; }

        public Double getDistance() { return distance; }
        public void setDistance(Double distance) { this.distance = distance; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LiveRouteStation {
        private Integer sequence;
        private String stationCode;
        private String stationName;
        private Boolean isHalt;
        private Double lat;
        private Double lng;
        private String scheduledArrival;
        private String scheduledDeparture;
        private String actualArrival;
        private String actualDeparture;
        private Integer delayArrival;
        private Integer delayDeparture;
        private String status;
        private Double distance;
        private Double speedToNextStationKmph;
        private String platform;
        private String coachPosition;

        public LiveRouteStation() {}

        public Integer getSequence() { return sequence; }
        public void setSequence(Integer sequence) { this.sequence = sequence; }

        public String getStationCode() { return stationCode; }
        public void setStationCode(String stationCode) { this.stationCode = stationCode; }

        public String getStationName() { return stationName; }
        public void setStationName(String stationName) { this.stationName = stationName; }

        public Boolean getIsHalt() { return isHalt; }
        public void setIsHalt(Boolean isHalt) { this.isHalt = isHalt; }

        public Double getLat() { return lat; }
        public void setLat(Double lat) { this.lat = lat; }

        public Double getLng() { return lng; }
        public void setLng(Double lng) { this.lng = lng; }

        public String getScheduledArrival() { return scheduledArrival; }
        public void setScheduledArrival(String scheduledArrival) { this.scheduledArrival = scheduledArrival; }

        public String getScheduledDeparture() { return scheduledDeparture; }
        public void setScheduledDeparture(String scheduledDeparture) { this.scheduledDeparture = scheduledDeparture; }

        public String getActualArrival() { return actualArrival; }
        public void setActualArrival(String actualArrival) { this.actualArrival = actualArrival; }

        public String getActualDeparture() { return actualDeparture; }
        public void setActualDeparture(String actualDeparture) { this.actualDeparture = actualDeparture; }

        public Integer getDelayArrival() { return delayArrival; }
        public void setDelayArrival(Integer delayArrival) { this.delayArrival = delayArrival; }

        public Integer getDelayDeparture() { return delayDeparture; }
        public void setDelayDeparture(Integer delayDeparture) { this.delayDeparture = delayDeparture; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Double getDistance() { return distance; }
        public void setDistance(Double distance) { this.distance = distance; }

        public Double getSpeedToNextStationKmph() { return speedToNextStationKmph; }
        public void setSpeedToNextStationKmph(Double speedToNextStationKmph) { this.speedToNextStationKmph = speedToNextStationKmph; }

        public String getPlatform() { return platform; }
        public void setPlatform(String platform) { this.platform = platform; }

        public String getCoachPosition() { return coachPosition; }
        public void setCoachPosition(String coachPosition) { this.coachPosition = coachPosition; }
    }
}


