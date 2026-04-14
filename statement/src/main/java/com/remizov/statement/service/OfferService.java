package com.remizov.statement.service;

import com.remizov.statement.dto.LoanOfferDto;

public interface OfferService {
    void selectOffer(LoanOfferDto offer);
}