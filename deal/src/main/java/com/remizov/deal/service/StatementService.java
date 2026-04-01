package com.remizov.deal.service;

import com.remizov.deal.dto.LoanOfferDto;
import com.remizov.deal.dto.LoanStatementRequestDto;

import java.util.List;

public interface StatementService {

    List<LoanOfferDto> statement(LoanStatementRequestDto request);
}