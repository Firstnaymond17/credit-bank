package com.remizov.deal.service.impl;

import com.remizov.deal.dto.LoanOfferDto;
import com.remizov.deal.entity.Statement;
import com.remizov.deal.enums.ApplicationStatus;
import com.remizov.deal.repository.StatementRepository;
import com.remizov.deal.service.OfferService;
import com.remizov.deal.utils.StatementUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {

    private final StatementRepository statementRepository;

    @Override
    public void selectOffer(LoanOfferDto request) {
        log.info("Получен запрос на выбор оффера: statementId={}, rate={}, isInsuranceEnabled={}",
                request.getStatementId(), request.getRate(), request.getIsInsuranceEnabled());

        Statement statement = statementRepository.findById(request.getStatementId())
                .orElseThrow(() -> new RuntimeException("Заявка не найдена " + request.getStatementId()));
        log.info("Заявка найдена: statementId={}", statement.getId());

        StatementUtils.updateStatementStatus(statement, ApplicationStatus.APPROVED);
        log.info("Статус заявки обновлён: statementId={}, status={}", statement.getId(), ApplicationStatus.APPROVED);

        statement.setAppliedOffer(request);
        statementRepository.save(statement);
        log.info("Выбранный оффер сохранён, заявка обновлена: statementId={}", statement.getId());
    }
}