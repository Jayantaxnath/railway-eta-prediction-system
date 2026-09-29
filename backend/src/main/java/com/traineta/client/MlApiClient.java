package com.traineta.client;

import com.traineta.dto.PythonMlRequest;
import com.traineta.dto.PythonMlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class MlApiClient {

    private static final Logger log = LoggerFactory.getLogger(MlApiClient.class);
    private final WebClient webClient;

    public MlApiClient(@Qualifier("mlWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<PythonMlResponse> predictEta(PythonMlRequest request) {
        log.info("Sending prediction request to Python FastAPI ML service for train {}", request.getTrainId());
        return webClient.post()
                .uri("/predict-eta")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PythonMlResponse.class)
                .timeout(Duration.ofSeconds(10))
                .doOnError(error -> log.error("Error communicating with Python ML service for train {}: {}", request.getTrainId(), error.getMessage()))
                .onErrorResume(error -> Mono.empty());
    }
}
