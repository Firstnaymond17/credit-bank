package com.remizov.statement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${deal.url}")
    private String dealUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(dealUrl)
                .build();
    }
}