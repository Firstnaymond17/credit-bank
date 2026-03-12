package com.remizov.calculator.service;

import com.remizov.calculator.dto.CreditDto;
import com.remizov.calculator.dto.LoanOfferDto;
import com.remizov.calculator.dto.LoanStatementRequestDto;
import com.remizov.calculator.dto.ScoringDataDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CalculatorService {

    List<LoanOfferDto> createOffers(LoanStatementRequestDto request);

    CreditDto createCredit(ScoringDataDto scoringData);
}