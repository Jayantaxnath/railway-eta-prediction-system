package com.traineta.client;

import com.traineta.dto.LiveMapSnapshotResponse;
import com.traineta.dto.RailwayApiResponse;
import com.traineta.dto.RailwayScheduleResponse;
import com.traineta.dto.RouteGeometryResponse;
import com.traineta.dto.StationLiveBoardResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class RailwayApiClient {

    private static final Logger log = LoggerFactory.getLogger(RailwayApiClient.class);
    private final WebClient webClient;

    public RailwayApiClient(@Qualifier("railwayWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<RailwayApiResponse> fetchLiveTrainStatus(String trainNumber, String date) {
        log.info("Fetching live status from RailRadar API for train: {} on date: {}", trainNumber, date);
        return webClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path("/trains/{number}/live");
                    if (date != null && !date.trim().isEmpty()) {
                        builder.queryParam("date", date.trim());
                    }
                    return builder.build(trainNumber);
                })
                .retrieve()
                .bodyToMono(RailwayApiResponse.class)
                .timeout(Duration.ofSeconds(10))
                .doOnError(error -> log.error("Error fetching live status for train {}: {}", trainNumber, error.getMessage()))
                .onErrorResume(error -> Mono.empty());
    }

    public Mono<RailwayApiResponse> fetchLiveTrainStatus(String trainNumber) {
        return fetchLiveTrainStatus(trainNumber, null);
    }

    public Mono<RailwayScheduleResponse> fetchTrainSchedule(String trainNumber) {
        log.info("Fetching timetable route schedule from RailRadar API for train: {}", trainNumber);
        return webClient.get()
                .uri("/trains/{number}", trainNumber)
                .retrieve()
                .bodyToMono(RailwayScheduleResponse.class)
                .timeout(Duration.ofSeconds(10))
                .doOnError(error -> log.error("Error fetching timetable schedule for train {}: {}", trainNumber, error.getMessage()))
                .onErrorResume(error -> Mono.empty());
    }

    public Mono<StationLiveBoardResponse> fetchLiveStationBoard(String stationCode, Integer hours) {
        log.info("Fetching live station board from RailRadar API for station: {} with hours window: {}", stationCode, hours);
        int hoursParam = (hours != null && (hours == 2 || hours == 4 || hours == 6 || hours == 8)) ? hours : 4;
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stations/{code}/live")
                        .queryParam("hours", hoursParam)
                        .build(stationCode))
                .retrieve()
                .bodyToMono(StationLiveBoardResponse.class)
                .timeout(Duration.ofSeconds(10))
                .doOnError(error -> log.error("Error fetching live station board for {}: {}", stationCode, error.getMessage()))
                .onErrorResume(error -> Mono.empty());
    }

    public Mono<Object> searchStations(String query) {
        log.info("Performing autocomplete search for station with query: {}", query);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/lookup/search/stations")
                        .queryParam("q", query)
                        .build())
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofSeconds(5))
                .doOnError(error -> log.error("Error searching stations for query {}: {}", query, error.getMessage()))
                .onErrorResume(error -> Mono.empty());
    }

    public Mono<Object> searchTrains(String query) {
        log.info("Performing autocomplete search for train with query: {}", query);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/lookup/search/trains")
                        .queryParam("q", query)
                        .build())
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofSeconds(5))
                .doOnError(error -> log.error("Error searching trains for query {}: {}", query, error.getMessage()))
                .onErrorResume(error -> Mono.empty());
    }

    public Mono<RouteGeometryResponse> fetchTrainRouteGeometry(String trainNumber) {
        log.info("Fetching GIS track geometry for train: {}", trainNumber);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/trains/{number}/route")
                        .queryParam("format", "geojson")
                        .queryParam("stops", "true")
                        .build(trainNumber))
                .retrieve()
                .bodyToMono(RouteGeometryResponse.class)
                .timeout(Duration.ofSeconds(10))
                .doOnError(error -> log.error("Error fetching route geometry for train {}: {}", trainNumber, error.getMessage()))
                .onErrorResume(error -> Mono.empty());
    }

    public Mono<LiveMapSnapshotResponse> fetchLiveMapSnapshot() {
        log.info("Fetching nationwide live map snapshot of active running trains from RailRadar API");
        return webClient.get()
                .uri("/legacy/trains/live-map")
                .retrieve()
                .bodyToMono(LiveMapSnapshotResponse.class)
                .timeout(Duration.ofSeconds(10))
                .doOnError(error -> log.error("Error fetching live map snapshot: {}", error.getMessage()))
                .onErrorResume(error -> Mono.empty());
    }
}

