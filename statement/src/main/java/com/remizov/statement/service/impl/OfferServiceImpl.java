package com.remizov.statement.service.impl;

import com.remizov.statement.client.DealClient;
import com.remizov.statement.dto.LoanOfferDto;
import com.remizov.statement.service.OfferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {

    private final DealClient dealClient;

    @Override
    public void selectOffer(LoanOfferDto offer) {
        log.info("Отправка выбранного оффера в МС deal: statementId={}, rate={}", offer.getStatementId(), offer.getRate());
        dealClient.selectOffer(offer);
        log.info("Оффер успешно отправлен в МС deal");
    }
}