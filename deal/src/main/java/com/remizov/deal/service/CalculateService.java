package com.remizov.deal.service;

import com.remizov.deal.dto.FinishRegistrationRequestDto;

public interface CalculateService {

    void calculate(String statementId, FinishRegistrationRequestDto request);
}