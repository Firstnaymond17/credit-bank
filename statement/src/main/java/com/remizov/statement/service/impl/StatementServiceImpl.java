package com.remizov.statement.service.impl;

import com.remizov.statement.client.DealClient;
import com.remizov.statement.dto.LoanOfferDto;
import com.remizov.statement.dto.LoanStatementRequestDto;
import com.remizov.statement.service.StatementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatementServiceImpl implements StatementService {

    private final DealClient dealClient;

    @Override
    public List<LoanOfferDto> getOffers(LoanStatementRequestDto request) {
        log.info("Отправка запроса в МС deal: amount={}, term={}", request.getAmount(), request.getTerm());
        List<LoanOfferDto> offers = dealClient.getOffers(request);
        log.info("Получено {} предложений от МС deal", offers.size());
        return offers;
    }
}