package com.remizov.statement.service;

import com.remizov.statement.dto.LoanOfferDto;
import com.remizov.statement.dto.LoanStatementRequestDto;

import java.util.List;

public interface StatementService {
    List<LoanOfferDto> getOffers(LoanStatementRequestDto request);
}
