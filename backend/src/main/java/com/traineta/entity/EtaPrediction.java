package com.traineta.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "eta_predictions")
public class EtaPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "train_number")
    private Train train;

    private LocalDateTime predictionTimestamp;
    private Integer currentDelayMinutes;
    private Double predictedRemainingMinutes;
    private LocalDateTime predictedEta;
    private Double confidenceScore;
    private String modelVersion;
    private String predictionSource;

    public EtaPrediction() {}

    public EtaPrediction(Long id, Train train, LocalDateTime predictionTimestamp, Integer currentDelayMinutes, Double predictedRemainingMinutes, LocalDateTime predictedEta, Double confidenceScore, String modelVersion, String predictionSource) {
        this.id = id;
        this.train = train;
        this.predictionTimestamp = predictionTimestamp;
        this.currentDelayMinutes = currentDelayMinutes;
        this.predictedRemainingMinutes = predictedRemainingMinutes;
        this.predictedEta = predictedEta;
        this.confidenceScore = confidenceScore;
        this.modelVersion = modelVersion;
        this.predictionSource = predictionSource;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Train getTrain() { return train; }
    public void setTrain(Train train) { this.train = train; }

    public LocalDateTime getPredictionTimestamp() { return predictionTimestamp; }
    public void setPredictionTimestamp(LocalDateTime predictionTimestamp) { this.predictionTimestamp = predictionTimestamp; }

    public Integer getCurrentDelayMinutes() { return currentDelayMinutes; }
    public void setCurrentDelayMinutes(Integer currentDelayMinutes) { this.currentDelayMinutes = currentDelayMinutes; }

    public Double getPredictedRemainingMinutes() { return predictedRemainingMinutes; }
    public void setPredictedRemainingMinutes(Double predictedRemainingMinutes) { this.predictedRemainingMinutes = predictedRemainingMinutes; }

    public LocalDateTime getPredictedEta() { return predictedEta; }
    public void setPredictedEta(LocalDateTime predictedEta) { this.predictedEta = predictedEta; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }

    public String getPredictionSource() { return predictionSource; }
    public void setPredictionSource(String predictionSource) { this.predictionSource = predictionSource; }

    public static EtaPredictionBuilder builder() { return new EtaPredictionBuilder(); }

    public static class EtaPredictionBuilder {
        private Long id;
        private Train train;
        private LocalDateTime predictionTimestamp;
        private Integer currentDelayMinutes;
        private Double predictedRemainingMinutes;
        private LocalDateTime predictedEta;
        private Double confidenceScore;
        private String modelVersion;
        private String predictionSource;

        public EtaPredictionBuilder id(Long id) { this.id = id; return this; }
        public EtaPredictionBuilder train(Train train) { this.train = train; return this; }
        public EtaPredictionBuilder predictionTimestamp(LocalDateTime predictionTimestamp) { this.predictionTimestamp = predictionTimestamp; return this; }
        public EtaPredictionBuilder currentDelayMinutes(Integer currentDelayMinutes) { this.currentDelayMinutes = currentDelayMinutes; return this; }
        public EtaPredictionBuilder predictedRemainingMinutes(Double predictedRemainingMinutes) { this.predictedRemainingMinutes = predictedRemainingMinutes; return this; }
        public EtaPredictionBuilder predictedEta(LocalDateTime predictedEta) { this.predictedEta = predictedEta; return this; }
        public EtaPredictionBuilder confidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; return this; }
        public EtaPredictionBuilder modelVersion(String modelVersion) { this.modelVersion = modelVersion; return this; }
        public EtaPredictionBuilder predictionSource(String predictionSource) { this.predictionSource = predictionSource; return this; }

        public EtaPrediction build() {
            return new EtaPrediction(id, train, predictionTimestamp, currentDelayMinutes, predictedRemainingMinutes, predictedEta, confidenceScore, modelVersion, predictionSource);
        }
    }
}

