package com.traineta.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${railway.api.url}")
    private String railwayApiUrl;

    @Value("${railway.api.key}")
    private String railwayApiKey;

    @Value("${ml.api.url:http://localhost:8000}")
    private String mlApiUrl;

    @Value("${openweather.api.url:https://api.openweathermap.org/data/2.5}")
    private String openWeatherApiUrl;

    @Bean
    public WebClient railwayWebClient() {
        return WebClient.builder()
                .baseUrl(railwayApiUrl)
                .defaultHeader("x-api-key", railwayApiKey)
                // The nationwide live-map snapshot is ~1 MB (2000+ trains), above the 256 KB default
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(5 * 1024 * 1024))
                .build();
    }

    @Bean
    public WebClient mlWebClient() {
        return WebClient.builder()
                .baseUrl(mlApiUrl)
                .build();
    }

    @Bean
    public WebClient openWeatherWebClient() {
        return WebClient.builder()
                .baseUrl(openWeatherApiUrl)
                .build();
    }
}
