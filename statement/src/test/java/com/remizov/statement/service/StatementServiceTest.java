package com.remizov.statement.service;

import com.remizov.statement.client.DealClient;
import com.remizov.statement.dto.LoanOfferDto;
import com.remizov.statement.dto.LoanStatementRequestDto;
import com.remizov.statement.service.impl.StatementServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatementServiceTest {

    @Mock
    private DealClient dealClient;

    @InjectMocks
    private StatementServiceImpl statementService;

    @Test
    void getOffers_delegatesToClient() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        List<LoanOfferDto> expected = List.of(LoanOfferDto.builder().build());
        when(dealClient.getOffers(request)).thenReturn(expected);

        List<LoanOfferDto> result = statementService.getOffers(request);

        assertEquals(expected, result);
        verify(dealClient).getOffers(request);
    }
}