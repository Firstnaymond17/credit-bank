package com.remizov.deal.service.impl;

import com.remizov.deal.dto.EmailMessage;
import com.remizov.deal.dto.LoanOfferDto;
import com.remizov.deal.entity.Statement;
import com.remizov.deal.entity.enums.ApplicationStatus;
import com.remizov.deal.entity.enums.EmailTheme;
import com.remizov.deal.repository.StatementRepository;
import com.remizov.deal.service.KafkaProducerService;
import com.remizov.deal.service.OfferService;
import com.remizov.deal.utils.StatementUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {

    private final StatementRepository statementRepository;
    private final KafkaProducerService kafkaProducerService;

    @Override
    @Transactional
    public void selectOffer(LoanOfferDto request) {
        log.info("Получен запрос на выбор оффера: statementId={}, rate={}, isInsuranceEnabled={}",
                request.getStatementId(), request.getRate(), request.getIsInsuranceEnabled());

        Statement statement = statementRepository.findByIdWithLock(request.getStatementId())
                .orElseThrow(() -> new RuntimeException("Заявка не найдена " + request.getStatementId()));
        log.info("Заявка найдена: statementId={}", statement.getId());

        StatementUtils.updateStatementStatus(statement, ApplicationStatus.APPROVED);
        log.info("Статус заявки обновлён: statementId={}, status={}", statement.getId(), ApplicationStatus.APPROVED);

        statement.setAppliedOffer(request);
        statementRepository.save(statement);
        log.info("Выбранный оффер сохранён, заявка обновлена: statementId={}", statement.getId());

        kafkaProducerService.send("finish-registration", EmailMessage.builder()
                .statementId(request.getStatementId())
                .theme(EmailTheme.FINISH_REGISTRATION)
                .address(statement.getClient().getEmail())
                .build());
        log.info("Сообщение отправлено в топик finish-registration: statementId={}", request.getStatementId());
    }
}