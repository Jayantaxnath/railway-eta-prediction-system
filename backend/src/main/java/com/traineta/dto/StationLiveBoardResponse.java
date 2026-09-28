package com.traineta.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StationLiveBoardResponse {

    private Boolean success;
    private LiveBoardData data;

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public LiveBoardData getData() { return data; }
    public void setData(LiveBoardData data) { this.data = data; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LiveBoardData {
        private StationMeta station;
        private Integer count;
        private List<BoardTrain> trains;

        public StationMeta getStation() { return station; }
        public void setStation(StationMeta station) { this.station = station; }

        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }

        public List<BoardTrain> getTrains() { return trains; }
        public void setTrains(List<BoardTrain> trains) { this.trains = trains; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StationMeta {
        private String code;
        private String name;

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BoardTrain {
        private TrainSummary train;
        private TrainStop stop;
        private LiveInfo live;

        public TrainSummary getTrain() { return train; }
        public void setTrain(TrainSummary train) { this.train = train; }

        public TrainStop getStop() { return stop; }
        public void setStop(TrainStop stop) { this.stop = stop; }

        public LiveInfo getLive() { return live; }
        public void setLive(LiveInfo live) { this.live = live; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TrainSummary {
        private String number;
        private String name;
        private String type;
        private String source;
        private String destination;
        private List<String> runDays;

        public String getNumber() { return number; }
        public void setNumber(String number) { this.number = number; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }

        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }

        public List<String> getRunDays() { return runDays; }
        public void setRunDays(List<String> runDays) { this.runDays = runDays; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TrainStop {
        private Integer sequence;
        private String arrival;
        private String departure;
        private Integer day;
        private Double distance;

        public Integer getSequence() { return sequence; }
        public void setSequence(Integer sequence) { this.sequence = sequence; }

        public String getArrival() { return arrival; }
        public void setArrival(String arrival) { this.arrival = arrival; }

        public String getDeparture() { return departure; }
        public void setDeparture(String departure) { this.departure = departure; }

        public Integer getDay() { return day; }
        public void setDay(Integer day) { this.day = day; }

        public Double getDistance() { return distance; }
        public void setDistance(Double distance) { this.distance = distance; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LiveInfo {
        private String type;
        private String expectedArrivalTime;
        private String expectedDepartureTime;
        private String platform;
        private Integer delayMinutes;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getExpectedArrivalTime() { return expectedArrivalTime; }
        public void setExpectedArrivalTime(String expectedArrivalTime) { this.expectedArrivalTime = expectedArrivalTime; }

        public String getExpectedDepartureTime() { return expectedDepartureTime; }
        public void setExpectedDepartureTime(String expectedDepartureTime) { this.expectedDepartureTime = expectedDepartureTime; }

        public String getPlatform() { return platform; }
        public void setPlatform(String platform) { this.platform = platform; }

        public Integer getDelayMinutes() { return delayMinutes; }
        public void setDelayMinutes(Integer delayMinutes) { this.delayMinutes = delayMinutes; }
    }
}
