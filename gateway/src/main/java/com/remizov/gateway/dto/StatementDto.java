package com.remizov.gateway.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class StatementDto {

    private UUID id;

    private Object client;

    private Object credit;

    private String status;

    private Object appliedOffer;
}