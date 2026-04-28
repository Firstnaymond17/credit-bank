package com.remizov.deal.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.apache.kafka.clients.admin.NewTopic;


@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic finishRegistration() {
        return TopicBuilder.name("finish-registration").build();
    }

    @Bean
    public NewTopic createDocuments() {
        return TopicBuilder.name("create-documents").build();
    }

    @Bean
    public NewTopic sendDocuments() {
        return TopicBuilder.name("send-documents").build();
    }

    @Bean
    public NewTopic sendSes() {
        return TopicBuilder.name("send-ses").build();
    }

    @Bean
    public NewTopic creditIssued() {
        return TopicBuilder.name("credit-issued").build();
    }

    @Bean
    public NewTopic statementDenied() {
        return TopicBuilder.name("statement-denied").build();
    }
}