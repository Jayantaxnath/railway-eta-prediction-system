package com.traineta.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "train_schedules")
public class TrainSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "train_number")
    private Train train;

    @ManyToOne
    @JoinColumn(name = "station_code")
    private Station station;

    private String scheduledArrival;
    private String scheduledDeparture;
    private Integer dayNumber;

    /**
     * Sequence number of this stop in the train's route (1 = origin, N = destination).
     * Used to guarantee correct stop ordering when computing stopsRemaining.
     */
    private Integer stopNumber;

    public TrainSchedule() {}

    public TrainSchedule(Long id, Train train, Station station, String scheduledArrival, String scheduledDeparture, Integer dayNumber, Integer stopNumber) {
        this.id = id;
        this.train = train;
        this.station = station;
        this.scheduledArrival = scheduledArrival;
        this.scheduledDeparture = scheduledDeparture;
        this.dayNumber = dayNumber;
        this.stopNumber = stopNumber;
    }

    public TrainSchedule(Train train, Station station, String scheduledArrival, String scheduledDeparture, Integer dayNumber) {
        this.train = train;
        this.station = station;
        this.scheduledArrival = scheduledArrival;
        this.scheduledDeparture = scheduledDeparture;
        this.dayNumber = dayNumber;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Train getTrain() { return train; }
    public void setTrain(Train train) { this.train = train; }

    public Station getStation() { return station; }
    public void setStation(Station station) { this.station = station; }

    public String getScheduledArrival() { return scheduledArrival; }
    public void setScheduledArrival(String scheduledArrival) { this.scheduledArrival = scheduledArrival; }

    public String getScheduledDeparture() { return scheduledDeparture; }
    public void setScheduledDeparture(String scheduledDeparture) { this.scheduledDeparture = scheduledDeparture; }

    public Integer getDayNumber() { return dayNumber; }
    public void setDayNumber(Integer dayNumber) { this.dayNumber = dayNumber; }

    public Integer getStopNumber() { return stopNumber; }
    public void setStopNumber(Integer stopNumber) { this.stopNumber = stopNumber; }
}

