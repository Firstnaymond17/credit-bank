package com.remizov.deal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${calculator.url}")
    private String calculatorUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(calculatorUrl)
                .build();
    }
}