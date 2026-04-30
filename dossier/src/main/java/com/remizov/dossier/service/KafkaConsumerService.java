package com.remizov.dossier.service;

import com.remizov.dossier.dto.EmailMessage;

public interface KafkaConsumerService {
    void handleFinishRegistration(EmailMessage message);
    void handleCreateDocuments(EmailMessage message);
    void handleSendDocuments(EmailMessage message);
    void handleSendSes(EmailMessage message);
    void handleCreditIssued(EmailMessage message);
    void handleStatementDenied(EmailMessage message);
}