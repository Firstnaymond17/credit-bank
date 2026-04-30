package com.remizov.deal.service.impl;

import com.remizov.deal.dto.EmailMessage;
import com.remizov.deal.entity.Statement;
import com.remizov.deal.entity.enums.EmailTheme;
import com.remizov.deal.repository.StatementRepository;
import com.remizov.deal.service.DocumentService;
import com.remizov.deal.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final KafkaProducerService kafkaProducerService;
    private final StatementRepository statementRepository;

    @Override
    public void sendDocuments(String statementId) {
        log.info("Запрос на отправку документов: statementId={}", statementId);
        Statement statement = getStatement(statementId);
        kafkaProducerService.send("send-documents", EmailMessage.builder()
                .statementId(UUID.fromString(statementId))
                .theme(EmailTheme.SEND_DOCUMENTS)
                .address(statement.getClient().getEmail())
                .build());
    }

    @Override
    public void signDocuments(String statementId) {
        log.info("Запрос на подписание документов: statementId={}", statementId);
        Statement statement = getStatement(statementId);
        kafkaProducerService.send("send-ses", EmailMessage.builder()
                .statementId(UUID.fromString(statementId))
                .theme(EmailTheme.SEND_SES)
                .address(statement.getClient().getEmail())
                .build());
    }

    @Override
    public void codeDocuments(String statementId) {
        log.info("Подписание документов по коду: statementId={}", statementId);
        Statement statement = getStatement(statementId);
        kafkaProducerService.send("credit-issued", EmailMessage.builder()
                .statementId(UUID.fromString(statementId))
                .theme(EmailTheme.CREDIT_ISSUED)
                .address(statement.getClient().getEmail())
                .build());
    }

    private Statement getStatement(String statementId) {
        return statementRepository.findById(UUID.fromString(statementId))
                .orElseThrow(() -> new RuntimeException("Заявка не найдена: " + statementId));
    }
}