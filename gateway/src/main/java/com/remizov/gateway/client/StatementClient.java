package com.remizov.gateway.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class StatementClient {

    private final RestClient statementRestClient;

    public <T> T getOffers(Object request) {
        return statementRestClient.post()
                .uri("/statement")
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public void selectOffer(Object offer) {
        statementRestClient.post()
                .uri("/statement/offer")
                .body(offer)
                .retrieve()
                .toBodilessEntity();
    }
}