package com.remizov.statement.service;

import com.remizov.statement.client.DealClient;
import com.remizov.statement.dto.LoanOfferDto;
import com.remizov.statement.service.impl.OfferServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    private DealClient dealClient;

    @InjectMocks
    private OfferServiceImpl offerService;

    @Test
    void selectOffer_delegatesToClient() {
        LoanOfferDto offer = LoanOfferDto.builder().build();

        offerService.selectOffer(offer);

        verify(dealClient).selectOffer(offer);
    }
}