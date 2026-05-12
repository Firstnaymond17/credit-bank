package com.remizov.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${statement.url}")
    private String statementUrl;

    @Value("${deal.url}")
    private String dealUrl;

    @Bean
    public RestClient statementRestClient() {
        return RestClient.builder()
                .baseUrl(statementUrl)
                .build();
    }

    @Bean
    public RestClient dealRestClient() {
        return RestClient.builder()
                .baseUrl(dealUrl)
                .build();
    }
}