package com.remizov.calculator.service;

import com.remizov.calculator.dto.*;
import com.remizov.calculator.properties.ScoringProperties;
import com.remizov.calculator.service.impl.OfferServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class OfferServiceTest {

    @Mock
    private ScoringProperties scoringProperties;

    @InjectMocks
    private OfferServiceImpl offerService;

    @BeforeEach
    void setUp() {
        when(scoringProperties.getBaseRate()).thenReturn(25.0);
    }

    @Test
    @DisplayName("createOffers — должен вернуть 4 оффера")
    void createOffers_returnsFourOffers() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(BigDecimal.valueOf(100_000));
        request.setTerm(12);

        List<LoanOfferDto> offers = offerService.createOffers(request);

        assertEquals(4, offers.size());
    }

    @Test
    @DisplayName("createOffers — ставки для всех 4 комбинаций верные")
    void createOffers_ratesAreCorrect() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(BigDecimal.valueOf(100_000));
        request.setTerm(12);

        List<LoanOfferDto> offers = offerService.createOffers(request);

        assertAll(
                () -> assertTrue(offers.stream().anyMatch(o ->
                        !o.getIsInsuranceEnabled() &&
                                !o.getIsSalaryClient() &&
                                o.getRate().compareTo(BigDecimal.valueOf(25)) == 0)),

                () -> assertTrue(offers.stream().anyMatch(o ->
                        o.getIsInsuranceEnabled() &&
                                !o.getIsSalaryClient() &&
                                o.getRate().compareTo(BigDecimal.valueOf(22)) == 0 &&
                                o.getTotalAmount().compareTo(BigDecimal.valueOf(105_000)) == 0)),

                () -> assertTrue(offers.stream().anyMatch(o ->
                        !o.getIsInsuranceEnabled() &&
                                o.getIsSalaryClient() &&
                                o.getRate().compareTo(BigDecimal.valueOf(24)) == 0)),

                () -> assertTrue(offers.stream().anyMatch(o ->
                        o.getIsInsuranceEnabled() &&
                                o.getIsSalaryClient() &&
                                o.getRate().compareTo(BigDecimal.valueOf(21)) == 0))
        );
    }
}