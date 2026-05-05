package com.remizov.gateway.client;

import com.remizov.gateway.dto.FinishRegistrationRequestDto;
import com.remizov.gateway.dto.StatementDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

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
                .uri("/deal/document/" + statementId + "/send")
                .retrieve()
                .toBodilessEntity();
    }

    public void signDocuments(String statementId) {
        dealRestClient.post()
                .uri("/deal/document/" + statementId + "/sign")
                .retrieve()
                .toBodilessEntity();
    }

    public void codeDocuments(String statementId) {
        dealRestClient.post()
                .uri("/deal/document/" + statementId + "/code")
                .retrieve()
                .toBodilessEntity();
    }

    public StatementDto getStatement(String statementId) {
        return dealRestClient.get()
                .uri("/deal/admin/statement/" + statementId)
                .retrieve()
                .body(StatementDto.class);
    }

    public List<StatementDto> getAllStatements() {
        return dealRestClient.get()
                .uri("/deal/admin/statement")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

}