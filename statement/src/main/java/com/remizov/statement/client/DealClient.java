package com.remizov.statement.client;

import com.remizov.statement.dto.LoanOfferDto;
import com.remizov.statement.dto.LoanStatementRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DealClient {

    private final RestClient restClient;

    public List<LoanOfferDto> getOffers(LoanStatementRequestDto request) {
        return restClient.post()
                .uri("/deal/statement")
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public void selectOffer(LoanOfferDto offer) {
        restClient.post()
                .uri("/deal/offer/select")
                .body(offer)
                .retrieve()
                .toBodilessEntity();
    }
}