package com.remizov.deal.dto;

import com.remizov.deal.entity.enums.EmailTheme;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class EmailMessage {
    private String address;
    private EmailTheme theme;
    private UUID statementId;
    private String text;
}