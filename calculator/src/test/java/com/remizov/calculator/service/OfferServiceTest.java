package com.remizov.calculator.service;

import com.remizov.calculator.dto.*;
import com.remizov.calculator.service.impl.OfferServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
public class OfferServiceTest {

    @InjectMocks
    private OfferServiceImpl offerService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(offerService, "baseRate", BigDecimal.valueOf(25));
    }

    @Test
    @DisplayName("createOffers — должен вернуть 4 оффера")
    void createOffers_returnsFourOffers() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(BigDecimal.valueOf(100_000));
        request.setTerm(12);

        List<LoanOfferDto> offers = offerService.createOffers(request);

        assertThat(offers).hasSize(4);
    }

    @Test
    @DisplayName("createOffers — ставки для всех 4 комбинаций верные")
    void createOffers_ratesAreCorrect() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(BigDecimal.valueOf(100_000));
        request.setTerm(12);

        List<LoanOfferDto> offers = offerService.createOffers(request);

        assertThat(offers).anySatisfy(o -> {
            assertThat(o.getIsInsuranceEnabled()).isFalse();
            assertThat(o.getIsSalaryClient()).isFalse();
            assertThat(o.getRate()).isEqualByComparingTo(BigDecimal.valueOf(25));
        });

        assertThat(offers).anySatisfy(o -> {
            assertThat(o.getIsInsuranceEnabled()).isTrue();
            assertThat(o.getIsSalaryClient()).isFalse();
            assertThat(o.getRate()).isEqualByComparingTo(BigDecimal.valueOf(22));
            assertThat(o.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(105_000));
        });

        assertThat(offers).anySatisfy(o -> {
            assertThat(o.getIsInsuranceEnabled()).isFalse();
            assertThat(o.getIsSalaryClient()).isTrue();
            assertThat(o.getRate()).isEqualByComparingTo(BigDecimal.valueOf(24));
        });

        assertThat(offers).anySatisfy(o -> {
            assertThat(o.getIsInsuranceEnabled()).isTrue();
            assertThat(o.getIsSalaryClient()).isTrue();
            assertThat(o.getRate()).isEqualByComparingTo(BigDecimal.valueOf(21));
        });
    }
}
