package com.traineta.dto;

import java.time.LocalDateTime;

public class MlPredictionResponse {

    private String trainNumber;
    private Double predictedRemainingMinutes;
    private Double confidenceScore;
    private String modelVersion;
    private String predictionSource;
    private LocalDateTime predictionTimestamp;

    public MlPredictionResponse() {}

    public MlPredictionResponse(String trainNumber, Double predictedRemainingMinutes, Double confidenceScore, String modelVersion, String predictionSource, LocalDateTime predictionTimestamp) {
        this.trainNumber = trainNumber;
        this.predictedRemainingMinutes = predictedRemainingMinutes;
        this.confidenceScore = confidenceScore;
        this.modelVersion = modelVersion;
        this.predictionSource = predictionSource;
        this.predictionTimestamp = predictionTimestamp;
    }

    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

    public Double getPredictedRemainingMinutes() { return predictedRemainingMinutes; }
    public void setPredictedRemainingMinutes(Double predictedRemainingMinutes) { this.predictedRemainingMinutes = predictedRemainingMinutes; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }

    public String getPredictionSource() { return predictionSource; }
    public void setPredictionSource(String predictionSource) { this.predictionSource = predictionSource; }

    public LocalDateTime getPredictionTimestamp() { return predictionTimestamp; }
    public void setPredictionTimestamp(LocalDateTime predictionTimestamp) { this.predictionTimestamp = predictionTimestamp; }

    public static MlPredictionResponseBuilder builder() { return new MlPredictionResponseBuilder(); }

    public static class MlPredictionResponseBuilder {
        private String trainNumber;
        private Double predictedRemainingMinutes;
        private Double confidenceScore;
        private String modelVersion;
        private String predictionSource;
        private LocalDateTime predictionTimestamp;

        public MlPredictionResponseBuilder trainNumber(String trainNumber) { this.trainNumber = trainNumber; return this; }
        public MlPredictionResponseBuilder predictedRemainingMinutes(Double predictedRemainingMinutes) { this.predictedRemainingMinutes = predictedRemainingMinutes; return this; }
        public MlPredictionResponseBuilder confidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; return this; }
        public MlPredictionResponseBuilder modelVersion(String modelVersion) { this.modelVersion = modelVersion; return this; }
        public MlPredictionResponseBuilder predictionSource(String predictionSource) { this.predictionSource = predictionSource; return this; }
        public MlPredictionResponseBuilder predictionTimestamp(LocalDateTime predictionTimestamp) { this.predictionTimestamp = predictionTimestamp; return this; }

        public MlPredictionResponse build() {
            return new MlPredictionResponse(trainNumber, predictedRemainingMinutes, confidenceScore, modelVersion, predictionSource, predictionTimestamp);
        }
    }
}

