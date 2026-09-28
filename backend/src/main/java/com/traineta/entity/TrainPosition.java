package com.traineta.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "train_positions")
public class TrainPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "train_number")
    private Train train;

    private Double latitude;
    private Double longitude;
    private Integer speed;
    private Integer delayMinutes;

    private String currentStation;
    private String nextStation;
    private String previousStation;

    private String journeyDate;
    private LocalDateTime timestamp;
    private String apiUpdateTimestamp;

    public TrainPosition() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Train getTrain() { return train; }
    public void setTrain(Train train) { this.train = train; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Integer getSpeed() { return speed; }
    public void setSpeed(Integer speed) { this.speed = speed; }

    public Integer getDelayMinutes() { return delayMinutes; }
    public void setDelayMinutes(Integer delayMinutes) { this.delayMinutes = delayMinutes; }

    public String getCurrentStation() { return currentStation; }
    public void setCurrentStation(String currentStation) { this.currentStation = currentStation; }

    public String getNextStation() { return nextStation; }
    public void setNextStation(String nextStation) { this.nextStation = nextStation; }

    public String getPreviousStation() { return previousStation; }
    public void setPreviousStation(String previousStation) { this.previousStation = previousStation; }

    public String getJourneyDate() { return journeyDate; }
    public void setJourneyDate(String journeyDate) { this.journeyDate = journeyDate; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getApiUpdateTimestamp() { return apiUpdateTimestamp; }
    public void setApiUpdateTimestamp(String apiUpdateTimestamp) { this.apiUpdateTimestamp = apiUpdateTimestamp; }
}

