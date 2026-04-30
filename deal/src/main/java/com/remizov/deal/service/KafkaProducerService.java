package com.remizov.deal.service;

import com.remizov.deal.dto.EmailMessage;

public interface KafkaProducerService {
    void send(String topic, EmailMessage message);
}