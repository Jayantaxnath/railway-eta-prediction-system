package com.traineta.controller;

import com.traineta.dto.FeatureRequest;
import com.traineta.service.FeatureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/trains")
public class FeatureController {

    private final FeatureService featureService;

    public FeatureController(FeatureService featureService) {
        this.featureService = featureService;
    }

    @GetMapping("/{trainNumber}/features")
    public ResponseEntity<FeatureRequest> getTrainFeatures(@PathVariable String trainNumber) {
        FeatureRequest features = featureService.buildFeaturesForTrain(trainNumber);
        return ResponseEntity.ok(features);
    }
}
