package com.traineta.controller;

import com.traineta.dto.EtaResponse;
import com.traineta.service.EtaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/trains")
public class EtaController {

    private final EtaService etaService;

    public EtaController(EtaService etaService) {
        this.etaService = etaService;
    }

    @GetMapping("/{trainNumber}/eta")
    public ResponseEntity<EtaResponse> getLatestEta(@PathVariable String trainNumber) {
        return ResponseEntity.ok(etaService.getLatestEta(trainNumber));
    }

    @GetMapping("/{trainNumber}/eta/history")
    public ResponseEntity<List<EtaResponse>> getEtaHistory(@PathVariable String trainNumber) {
        return ResponseEntity.ok(etaService.getEtaHistory(trainNumber));
    }

    @PostMapping("/{trainNumber}/eta/predict")
    public ResponseEntity<EtaResponse> triggerEtaPrediction(@PathVariable String trainNumber) {
        return ResponseEntity.ok(etaService.generateAndSaveEta(trainNumber));
    }
}
