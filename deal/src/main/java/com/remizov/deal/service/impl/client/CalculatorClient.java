package com.remizov.deal.service.impl.client;

import com.remizov.deal.dto.CreditDto;
import com.remizov.deal.dto.LoanOfferDto;
import com.remizov.deal.dto.LoanStatementRequestDto;
import com.remizov.deal.dto.ScoringDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CalculatorClient {

    private final RestClient restClient;

    public List<LoanOfferDto> getOffers(LoanStatementRequestDto request) {
        return restClient.post()
                .uri("/calculator/offers")
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public CreditDto calculateCredit(ScoringDataDto scoringData) {
        return restClient.post()
                .uri("/calculator/calc")
                .body(scoringData)
                .retrieve()
                .body(CreditDto.class);
    }
}