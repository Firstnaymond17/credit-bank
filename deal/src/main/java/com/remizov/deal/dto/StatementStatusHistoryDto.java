package com.remizov.deal.dto;

import com.remizov.deal.enums.ApplicationStatus;
import com.remizov.deal.enums.ChangeType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatementStatusHistoryDto {

    private ApplicationStatus status;

    private LocalDateTime time;

    private ChangeType changeType;
}