package com.traineta.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RailwayScheduleResponse {

    private Boolean success;
    private RailRadarScheduleData data;

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public RailRadarScheduleData getData() { return data; }
    public void setData(RailRadarScheduleData data) { this.data = data; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RailRadarScheduleData {
        private TrainMetadata train;
        private List<RouteStationItem> route;

        public TrainMetadata getTrain() { return train; }
        public void setTrain(TrainMetadata train) { this.train = train; }

        public List<RouteStationItem> getRoute() { return route; }
        public void setRoute(List<RouteStationItem> route) { this.route = route; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TrainMetadata {
        private String number;
        private String name;
        private String type;
        private String category;
        private StationRef source;
        private StationRef destination;
        private Integer distance;
        private Integer duration;
        private Double avgSpeed;
        private Integer totalHalts;
        private String returnTrain;
        private String coachPosition;

        public String getNumber() { return number; }
        public void setNumber(String number) { this.number = number; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public StationRef getSource() { return source; }
        public void setSource(StationRef source) { this.source = source; }

        public StationRef getDestination() { return destination; }
        public void setDestination(StationRef destination) { this.destination = destination; }

        public Integer getDistance() { return distance; }
        public void setDistance(Integer distance) { this.distance = distance; }

        public Integer getDuration() { return duration; }
        public void setDuration(Integer duration) { this.duration = duration; }

        public Double getAvgSpeed() { return avgSpeed; }
        public void setAvgSpeed(Double avgSpeed) { this.avgSpeed = avgSpeed; }

        public Integer getTotalHalts() { return totalHalts; }
        public void setTotalHalts(Integer totalHalts) { this.totalHalts = totalHalts; }

        public String getReturnTrain() { return returnTrain; }
        public void setReturnTrain(String returnTrain) { this.returnTrain = returnTrain; }

        public String getCoachPosition() { return coachPosition; }
        public void setCoachPosition(String coachPosition) { this.coachPosition = coachPosition; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StationRef {
        private String code;
        private String name;

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RouteStationItem {
        private Integer sequence;
        private StationRef station;
        private String arrival;
        private String departure;
        private Integer arrivalDay;
        private Integer departureDay;
        private Double distance;
        private Boolean isHalt;
        private String platform;
        private Double speedToNextStationKmph;

        public Integer getSequence() { return sequence; }
        public void setSequence(Integer sequence) { this.sequence = sequence; }

        public StationRef getStation() { return station; }
        public void setStation(StationRef station) { this.station = station; }

        public String getArrival() { return arrival; }
        public void setArrival(String arrival) { this.arrival = arrival; }

        public String getDeparture() { return departure; }
        public void setDeparture(String departure) { this.departure = departure; }

        public Integer getArrivalDay() { return arrivalDay; }
        public void setArrivalDay(Integer arrivalDay) { this.arrivalDay = arrivalDay; }

        public Integer getDepartureDay() { return departureDay; }
        public void setDepartureDay(Integer departureDay) { this.departureDay = departureDay; }

        public Double getDistance() { return distance; }
        public void setDistance(Double distance) { this.distance = distance; }

        public Boolean getIsHalt() { return isHalt; }
        public void setIsHalt(Boolean isHalt) { this.isHalt = isHalt; }

        public String getPlatform() { return platform; }
        public void setPlatform(String platform) { this.platform = platform; }

        public Double getSpeedToNextStationKmph() { return speedToNextStationKmph; }
        public void setSpeedToNextStationKmph(Double speedToNextStationKmph) { this.speedToNextStationKmph = speedToNextStationKmph; }
    }
}

