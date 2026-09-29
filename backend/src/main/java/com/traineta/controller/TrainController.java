package com.traineta.controller;

import com.traineta.client.RailwayApiClient;
import com.traineta.dto.LiveMapSnapshotResponse;
import com.traineta.dto.LiveTrainResponse;
import com.traineta.dto.RailwayApiResponse;
import com.traineta.dto.RailwayScheduleResponse;
import com.traineta.dto.RouteGeometryResponse;
import com.traineta.dto.TrainResponse;
import com.traineta.service.TrainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/trains")
public class TrainController {
    private final TrainService trainService;
    private final RailwayApiClient railwayApiClient;

    public TrainController(TrainService trainService, RailwayApiClient railwayApiClient) {
        this.trainService = trainService;
        this.railwayApiClient = railwayApiClient;
    }

    @GetMapping("/live-map")
    public ResponseEntity<LiveMapSnapshotResponse> getLiveMapSnapshot() {
        LiveMapSnapshotResponse snapshot = railwayApiClient.fetchLiveMapSnapshot().block();
        if (snapshot != null && snapshot.getData() != null) {
            return ResponseEntity.ok(snapshot);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchTrains(@RequestParam String q) {
        Object res = railwayApiClient.searchTrains(q).block();
        if (res != null) {
            return ResponseEntity.ok(res);
        }
        return ResponseEntity.ok(List.of());
    }

    @GetMapping
    public ResponseEntity<List<TrainResponse>> getAllTrains() {
        return ResponseEntity.ok(trainService.getAllTrains());
    }

    @GetMapping("/{trainNumber}")
    public ResponseEntity<TrainResponse> getTrainByNumber(@PathVariable String trainNumber) {
        return ResponseEntity.ok(trainService.getTrainByNumber(trainNumber));
    }

    @GetMapping("/{trainNumber}/live")
    public ResponseEntity<LiveTrainResponse> getLatestLivePosition(@PathVariable String trainNumber) {
        return ResponseEntity.ok(trainService.getLatestLivePosition(trainNumber));
    }

    @GetMapping("/{trainNumber}/live-data")
    public ResponseEntity<RailwayApiResponse.RailRadarData> getLiveTrainData(
            @PathVariable String trainNumber,
            @RequestParam(required = false) String date) {
        RailwayApiResponse.RailRadarData liveData = trainService.getLiveTrainData(trainNumber, date);
        if (liveData != null) {
            return ResponseEntity.ok(liveData);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{trainNumber}/schedule")
    public ResponseEntity<RailwayScheduleResponse.RailRadarScheduleData> getTrainSchedule(@PathVariable String trainNumber) {
        RailwayScheduleResponse.RailRadarScheduleData scheduleData = trainService.getTrainSchedule(trainNumber);
        if (scheduleData != null) {
            return ResponseEntity.ok(scheduleData);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{trainNumber}/geometry")
    public ResponseEntity<RouteGeometryResponse> getTrainRouteGeometry(@PathVariable String trainNumber) {
        RouteGeometryResponse geom = railwayApiClient.fetchTrainRouteGeometry(trainNumber).block();
        if (geom != null && geom.getData() != null) {
            return ResponseEntity.ok(geom);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{trainNumber}/positions")
    public ResponseEntity<List<LiveTrainResponse>> getStoredPositions(@PathVariable String trainNumber) {
        return ResponseEntity.ok(trainService.getStoredPositions(trainNumber));
    }
}

