package com.remizov.calculator.service;

import com.remizov.calculator.dto.CreditDto;
import com.remizov.calculator.dto.ScoringDataDto;


public interface CreditService {
    CreditDto createCredit(ScoringDataDto scoringData);
}
