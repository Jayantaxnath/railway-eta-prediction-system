package com.traineta.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WeatherApiClient {

    private static final Logger log = LoggerFactory.getLogger(WeatherApiClient.class);

    private final WebClient openWeatherWebClient;

    @Value("${openweather.api.key}")
    private String apiKey;

    public WeatherApiClient(WebClient openWeatherWebClient) {
        this.openWeatherWebClient = openWeatherWebClient;
    }

    public Double fetchRainfallMmByCoordinates(Double lat, Double lon) {
        if (lat == null || lon == null) return 0.0;
        try {
            log.info("Fetching real-time weather from OpenWeatherMap for lat: {}, lon: {}", lat, lon);
            JsonNode response = openWeatherWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/weather")
                            .queryParam("lat", lat)
                            .queryParam("lon", lon)
                            .queryParam("appid", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            return extractRainfallMm(response);
        } catch (Exception e) {
            log.warn("Failed to fetch weather data for coordinates ({}, {}): {}", lat, lon, e.getMessage());
            return 0.0;
        }
    }

    public Double fetchRainfallMmByCity(String stationName) {
        if (stationName == null || stationName.isBlank()) return 0.0;
        try {
            log.info("Fetching real-time weather from OpenWeatherMap for station/city: {}", stationName);
            JsonNode response = openWeatherWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/weather")
                            .queryParam("q", stationName + ",IN")
                            .queryParam("appid", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            return extractRainfallMm(response);
        } catch (Exception e) {
            log.warn("Failed to fetch weather data for city '{}': {}", stationName, e.getMessage());
            return 0.0;
        }
    }

    private Double extractRainfallMm(JsonNode response) {
        if (response != null && response.has("rain")) {
            JsonNode rain = response.get("rain");
            if (rain.has("1h")) {
                return rain.get("1h").asDouble(0.0);
            } else if (rain.has("3h")) {
                return rain.get("3h").asDouble(0.0);
            }
        }
        return 0.0;
    }
}
