package com.remizov.dossier.service.impl;

import com.remizov.dossier.dto.EmailMessage;
import com.remizov.dossier.service.EmailService;
import com.remizov.dossier.service.KafkaConsumerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    private final EmailService emailService;

    @Override
    @KafkaListener(topics = "finish-registration", groupId = "dossier-group")
    public void handleFinishRegistration(EmailMessage message) {
        log.info("Получено сообщение из топика finish-registration: {}", message);
        emailService.sendEmail(message.getAddress(), "Завершение регистрации", "Пожалуйста, завершите регистрацию.");
    }

    @Override
    @KafkaListener(topics = "create-documents", groupId = "dossier-group")
    public void handleCreateDocuments(EmailMessage message) {
        log.info("Получено сообщение из топика create-documents: {}", message);
        emailService.sendEmail(message.getAddress(), "Создание документов", "Ваши документы создаются.");
    }

    @Override
    @KafkaListener(topics = "send-documents", groupId = "dossier-group")
    public void handleSendDocuments(EmailMessage message) {
        log.info("Получено сообщение из топика send-documents: {}", message);
        emailService.sendEmail(message.getAddress(), "Отправка документов", "Ваши документы отправлены.");
    }

    @Override
    @KafkaListener(topics = "send-ses", groupId = "dossier-group")
    public void handleSendSes(EmailMessage message) {
        log.info("Получено сообщение из топика send-ses: {}", message);
        emailService.sendEmail(message.getAddress(), "Подписание документов", "Пожалуйста, подпишите документы.");
    }

    @Override
    @KafkaListener(topics = "credit-issued", groupId = "dossier-group")
    public void handleCreditIssued(EmailMessage message) {
        log.info("Получено сообщение из топика credit-issued: {}", message);
        emailService.sendEmail(message.getAddress(), "Кредит выдан", "Ваш кредит успешно выдан.");
    }

    @Override
    @KafkaListener(topics = "statement-denied", groupId = "dossier-group")
    public void handleStatementDenied(EmailMessage message) {
        log.info("Получено сообщение из топика statement-denied: {}", message);
        emailService.sendEmail(message.getAddress(), "Заявка отклонена", "К сожалению, ваша заявка отклонена.");
    }
}