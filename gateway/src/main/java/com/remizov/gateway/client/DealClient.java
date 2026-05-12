package com.remizov.gateway.client;

import com.remizov.gateway.dto.FinishRegistrationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


@Component
@RequiredArgsConstructor
public class DealClient {

    private final RestClient dealRestClient;

    public void calculate(String statementId, FinishRegistrationRequestDto request) {
        dealRestClient.post()
                .uri("/deal/calculate/{statementId}", statementId)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void sendDocuments(String statementId) {
        dealRestClient.post()
                .uri("/deal/document/{statementId}/send", statementId)
                .retrieve()
                .toBodilessEntity();
    }

    public void signDocuments(String statementId) {
        dealRestClient.post()
                .uri("/deal/document/{statementId}/sign", statementId)
                .retrieve()
                .toBodilessEntity();
    }

    public void codeDocuments(String statementId) {
        dealRestClient.post()
                .uri("/deal/document/{statementId}/code", statementId)
                .retrieve()
                .toBodilessEntity();
    }

}