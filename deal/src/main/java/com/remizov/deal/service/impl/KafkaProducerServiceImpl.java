package com.remizov.deal.service.impl;

import com.remizov.deal.dto.EmailMessage;
import com.remizov.deal.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void send(String topic, EmailMessage message) {
        log.info("Отправка сообщения в топик {}: {}", topic, message);
        kafkaTemplate.send(topic, message);
    }
}