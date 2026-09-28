package com.traineta.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PythonMlResponse {

    @JsonProperty("train_id")
    private String trainId;

    @JsonProperty("target_station_id")
    private String targetStationId;

    @JsonProperty("predicted_remaining_minutes")
    private Double predictedRemainingMinutes;

    @JsonProperty("lower_bound_minutes")
    private Double lowerBoundMinutes;

    @JsonProperty("upper_bound_minutes")
    private Double upperBoundMinutes;

    @JsonProperty("confidence_score")
    private Double confidenceScore;

    @JsonProperty("model_version")
    private String modelVersion;

    public PythonMlResponse() {}

    public PythonMlResponse(String trainId, String targetStationId, Double predictedRemainingMinutes, Double lowerBoundMinutes, Double upperBoundMinutes, String modelVersion) {
        this(trainId, targetStationId, predictedRemainingMinutes, lowerBoundMinutes, upperBoundMinutes, null, modelVersion);
    }

    public PythonMlResponse(String trainId, String targetStationId, Double predictedRemainingMinutes, Double lowerBoundMinutes, Double upperBoundMinutes, Double confidenceScore, String modelVersion) {
        this.trainId = trainId;
        this.targetStationId = targetStationId;
        this.predictedRemainingMinutes = predictedRemainingMinutes;
        this.lowerBoundMinutes = lowerBoundMinutes;
        this.upperBoundMinutes = upperBoundMinutes;
        this.confidenceScore = confidenceScore;
        this.modelVersion = modelVersion;
    }

    public String getTrainId() { return trainId; }
    public void setTrainId(String trainId) { this.trainId = trainId; }

    public String getTargetStationId() { return targetStationId; }
    public void setTargetStationId(String targetStationId) { this.targetStationId = targetStationId; }

    public Double getPredictedRemainingMinutes() { return predictedRemainingMinutes; }
    public void setPredictedRemainingMinutes(Double predictedRemainingMinutes) { this.predictedRemainingMinutes = predictedRemainingMinutes; }

    public Double getLowerBoundMinutes() { return lowerBoundMinutes; }
    public void setLowerBoundMinutes(Double lowerBoundMinutes) { this.lowerBoundMinutes = lowerBoundMinutes; }

    public Double getUpperBoundMinutes() { return upperBoundMinutes; }
    public void setUpperBoundMinutes(Double upperBoundMinutes) { this.upperBoundMinutes = upperBoundMinutes; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }
}

