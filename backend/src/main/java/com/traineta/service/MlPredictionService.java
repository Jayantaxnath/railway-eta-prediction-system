package com.traineta.service;

import com.traineta.dto.FeatureRequest;
import com.traineta.dto.MlPredictionResponse;

public interface MlPredictionService {

    /**
     * Predicts remaining travel time based on ML feature payload.
     */
    MlPredictionResponse predict(FeatureRequest features);
}
