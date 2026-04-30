package com.remizov.dossier.dto;

import com.remizov.dossier.dto.enums.EmailTheme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage {
    private String address;
    private EmailTheme theme;
    private UUID statementId;
    private String text;
}