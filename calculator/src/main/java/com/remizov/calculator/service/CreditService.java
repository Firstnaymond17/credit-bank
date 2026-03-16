package com.remizov.calculator.service;

import com.remizov.calculator.dto.CreditDto;
import com.remizov.calculator.dto.LoanOfferDto;
import com.remizov.calculator.dto.LoanStatementRequestDto;
import com.remizov.calculator.dto.ScoringDataDto;

import java.util.List;

public interface CreditService {
    CreditDto createCredit(ScoringDataDto scoringData);
}
