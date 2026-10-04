package com.traineta.client;

import com.traineta.dto.RailwayApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RailwayApiClientTest {

    private WebClient webClient;
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    private WebClient.ResponseSpec responseSpec;
    private RailwayApiClient railwayApiClient;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        webClient = mock(WebClient.class);
        requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        railwayApiClient = new RailwayApiClient(webClient);
    }

    @Test
    void testFetchLiveTrainStatus_Success() {
        RailwayApiResponse.RailRadarData data = new RailwayApiResponse.RailRadarData();
        data.setTrainNumber("12919");
        data.setTrainName("Malwa SF Express");
        data.setDelayMinutes(54);

        RailwayApiResponse mockResponse = new RailwayApiResponse(true, data);

        when(responseSpec.bodyToMono(RailwayApiResponse.class)).thenReturn(Mono.just(mockResponse));

        Mono<RailwayApiResponse> result = railwayApiClient.fetchLiveTrainStatus("12919");

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getSuccess() && "12919".equals(response.getData().getTrainNumber()))
                .verifyComplete();
    }
}
