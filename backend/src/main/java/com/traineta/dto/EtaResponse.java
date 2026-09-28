package com.traineta.dto;

import java.time.LocalDateTime;

public class EtaResponse {

    private String trainNumber;
    private Integer currentDelayMinutes;
    private Double predictedRemainingMinutes;
    private LocalDateTime predictedEta;
    private Double confidenceScore;
    private String predictionSource;
    private String modelVersion;
    private LocalDateTime predictionTimestamp;

    public EtaResponse() {}

    public EtaResponse(String trainNumber, Integer currentDelayMinutes, Double predictedRemainingMinutes, LocalDateTime predictedEta, Double confidenceScore, String predictionSource, String modelVersion, LocalDateTime predictionTimestamp) {
        this.trainNumber = trainNumber;
        this.currentDelayMinutes = currentDelayMinutes;
        this.predictedRemainingMinutes = predictedRemainingMinutes;
        this.predictedEta = predictedEta;
        this.confidenceScore = confidenceScore;
        this.predictionSource = predictionSource;
        this.modelVersion = modelVersion;
        this.predictionTimestamp = predictionTimestamp;
    }

    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

    public Integer getCurrentDelayMinutes() { return currentDelayMinutes; }
    public void setCurrentDelayMinutes(Integer currentDelayMinutes) { this.currentDelayMinutes = currentDelayMinutes; }

    public Double getPredictedRemainingMinutes() { return predictedRemainingMinutes; }
    public void setPredictedRemainingMinutes(Double predictedRemainingMinutes) { this.predictedRemainingMinutes = predictedRemainingMinutes; }

    public LocalDateTime getPredictedEta() { return predictedEta; }
    public void setPredictedEta(LocalDateTime predictedEta) { this.predictedEta = predictedEta; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getPredictionSource() { return predictionSource; }
    public void setPredictionSource(String predictionSource) { this.predictionSource = predictionSource; }

    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }

    public LocalDateTime getPredictionTimestamp() { return predictionTimestamp; }
    public void setPredictionTimestamp(LocalDateTime predictionTimestamp) { this.predictionTimestamp = predictionTimestamp; }

    public static EtaResponseBuilder builder() { return new EtaResponseBuilder(); }

    public static class EtaResponseBuilder {
        private String trainNumber;
        private Integer currentDelayMinutes;
        private Double predictedRemainingMinutes;
        private LocalDateTime predictedEta;
        private Double confidenceScore;
        private String predictionSource;
        private String modelVersion;
        private LocalDateTime predictionTimestamp;

        public EtaResponseBuilder trainNumber(String trainNumber) { this.trainNumber = trainNumber; return this; }
        public EtaResponseBuilder currentDelayMinutes(Integer currentDelayMinutes) { this.currentDelayMinutes = currentDelayMinutes; return this; }
        public EtaResponseBuilder predictedRemainingMinutes(Double predictedRemainingMinutes) { this.predictedRemainingMinutes = predictedRemainingMinutes; return this; }
        public EtaResponseBuilder predictedEta(LocalDateTime predictedEta) { this.predictedEta = predictedEta; return this; }
        public EtaResponseBuilder confidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; return this; }
        public EtaResponseBuilder predictionSource(String predictionSource) { this.predictionSource = predictionSource; return this; }
        public EtaResponseBuilder modelVersion(String modelVersion) { this.modelVersion = modelVersion; return this; }
        public EtaResponseBuilder predictionTimestamp(LocalDateTime predictionTimestamp) { this.predictionTimestamp = predictionTimestamp; return this; }

        public EtaResponse build() {
            return new EtaResponse(trainNumber, currentDelayMinutes, predictedRemainingMinutes, predictedEta, confidenceScore, predictionSource, modelVersion, predictionTimestamp);
        }
    }
}

