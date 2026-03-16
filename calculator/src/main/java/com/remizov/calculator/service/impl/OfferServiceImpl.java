package com.remizov.calculator.service.impl;

import com.remizov.calculator.dto.*;
import com.remizov.calculator.service.OfferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

import static com.remizov.calculator.service.impl.utils.MonthlyPaymentUtils.calculateMonthlyPayment;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {
    @Value("${base-rate}")
    private BigDecimal baseRate;

    @Override
    public List<LoanOfferDto> createOffers(LoanStatementRequestDto request) {
        log.info("Получен запрос на создание предложений: amount={}, term={}", request.getAmount(), request.getTerm());

        List<LoanOfferDto> offers = List.of(
                buildOffer(request, false, false),
                buildOffer(request, false, true),
                buildOffer(request, true, false),
                buildOffer(request, true, true));

        log.info("Сформировано {} кредитных предложений", offers.size());
        return offers;
    }

    private LoanOfferDto buildOffer(LoanStatementRequestDto request,
                                    boolean isInsuranceEnabled,
                                    boolean isSalaryClient) {
        BigDecimal rate = baseRate;
        if (isInsuranceEnabled) rate = rate.subtract(BigDecimal.valueOf(3));
        if (isSalaryClient) rate = rate.subtract(BigDecimal.valueOf(1));

        BigDecimal requestedAmount = request.getAmount();
        BigDecimal totalAmount = requestedAmount;
        if (isInsuranceEnabled) {
            BigDecimal insuranceCost = requestedAmount.multiply(BigDecimal.valueOf(0.05));
            totalAmount = requestedAmount.add(insuranceCost);
        }

        BigDecimal monthlyPayment = calculateMonthlyPayment(totalAmount, rate, request.getTerm());

        log.debug("Оффер сформирован: insurance={}, salaryClient={}, rate={}, totalAmount={}, monthlyPayment={}",
                isInsuranceEnabled, isSalaryClient, rate, totalAmount, monthlyPayment);

        return LoanOfferDto.builder()
                .statementId(UUID.randomUUID())
                .requestedAmount(requestedAmount)
                .totalAmount(totalAmount)
                .term(request.getTerm())
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();
    }

}