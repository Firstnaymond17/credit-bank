package com.remizov.calculator.service;

import com.remizov.calculator.dto.LoanOfferDto;
import com.remizov.calculator.dto.LoanStatementRequestDto;

import java.util.List;

public interface OfferService {
    List<LoanOfferDto> createOffers(LoanStatementRequestDto request);
}
