package com.traineta.controller;

import com.traineta.dto.FeatureRequest;
import com.traineta.dto.MlPredictionResponse;
import com.traineta.service.FeatureService;
import com.traineta.service.MlPredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/trains")
public class PredictionController {

    private final FeatureService featureService;
    private final MlPredictionService mlPredictionService;

    public PredictionController(FeatureService featureService, MlPredictionService mlPredictionService) {
        this.featureService = featureService;
        this.mlPredictionService = mlPredictionService;
    }

    @GetMapping("/{trainNumber}/prediction")
    public ResponseEntity<MlPredictionResponse> getPrediction(@PathVariable String trainNumber) {
        FeatureRequest features = featureService.buildFeaturesForTrain(trainNumber);
        MlPredictionResponse response = mlPredictionService.predict(features);
        return ResponseEntity.ok(response);
    }
}
