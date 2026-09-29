package com.traineta.controller;

import com.traineta.client.RailwayApiClient;
import com.traineta.dto.StationLiveBoardResponse;
import com.traineta.entity.Station;
import com.traineta.repository.StationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/stations")
public class StationController {

    private final StationRepository stationRepository;
    private final RailwayApiClient railwayApiClient;

    public StationController(StationRepository stationRepository, RailwayApiClient railwayApiClient) {
        this.stationRepository = stationRepository;
        this.railwayApiClient = railwayApiClient;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchStations(@RequestParam String q) {
        Object res = railwayApiClient.searchStations(q).block();
        if (res != null) {
            return ResponseEntity.ok(res);
        }
        return ResponseEntity.ok(List.of());
    }

    @GetMapping
    public ResponseEntity<List<Station>> getAllStations() {
        return ResponseEntity.ok(stationRepository.findAll());
    }

    @GetMapping("/{stationCode}")
    public ResponseEntity<Station> getStation(@PathVariable String stationCode) {
        return stationRepository.findById(stationCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{stationCode}/live")
    public ResponseEntity<StationLiveBoardResponse> getLiveStationBoard(
            @PathVariable String stationCode,
            @RequestParam(required = false, defaultValue = "4") Integer hours) {
        StationLiveBoardResponse response = railwayApiClient.fetchLiveStationBoard(stationCode, hours).block();
        if (response != null && response.getData() != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }
}
