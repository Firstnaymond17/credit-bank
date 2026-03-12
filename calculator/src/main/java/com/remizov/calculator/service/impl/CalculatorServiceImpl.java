package com.remizov.calculator.service.impl;

import com.remizov.calculator.dto.*;
import com.remizov.calculator.exception.ScoringException;
import com.remizov.calculator.service.CalculatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculatorServiceImpl implements CalculatorService {
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

    private BigDecimal calculateMonthlyPayment(BigDecimal totalAmount, BigDecimal rate, Integer term) {
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        BigDecimal pow = monthlyRate.add(BigDecimal.ONE).pow(term);
        BigDecimal numerator = monthlyRate.multiply(pow);
        BigDecimal denominator = pow.subtract(BigDecimal.ONE);
        return totalAmount.multiply(numerator.divide(denominator, 10, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
    }


    @Override
    public CreditDto createCredit(ScoringDataDto request) {
        log.info("Получен запрос на расчёт кредита: amount={}, term={}", request.getAmount(), request.getTerm());

        BigDecimal rate = baseRate;
        rate = scoring(request, rate);
        log.debug("Итоговая ставка после скоринга: rate={}", rate);

        BigDecimal monthlyPayment = calculateMonthlyPayment(request.getAmount(), rate, request.getTerm());
        log.debug("Ежемесячный платёж: monthlyPayment={}", monthlyPayment);

        BigDecimal psk = monthlyPayment.multiply(BigDecimal.valueOf(request.getTerm()));
        log.debug("Полная стоимость кредита: psk={}", psk);

        List<PaymentScheduleElementDto> schedule = buildPaymentSchedule(request.getAmount(), rate, request.getTerm(), monthlyPayment);
        log.debug("График платежей сформирован, количество элементов: {}", schedule.size());

        CreditDto credit = CreditDto.builder()
                .amount(request.getAmount())
                .term(request.getTerm())
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .psk(psk)
                .isInsuranceEnabled(request.getIsInsuranceEnabled())
                .isSalaryClient(request.getIsSalaryClient())
                .paymentSchedule(schedule)
                .build();

        log.info("Кредит рассчитан: amount={}, rate={}, monthlyPayment={}, psk={}",
                credit.getAmount(), credit.getRate(), credit.getMonthlyPayment(), credit.getPsk());
        return credit;
    }

    private BigDecimal scoring(ScoringDataDto request, BigDecimal rate) {
        if (request.getIsInsuranceEnabled()) rate = rate.subtract(BigDecimal.valueOf(3));
        if (request.getIsSalaryClient()) rate = rate.subtract(BigDecimal.ONE);

        EmploymentDto emp = request.getEmployment();

        switch (emp.getEmploymentStatus()) {
            case UNEMPLOYED -> {
                log.warn("Скоринг не пройден: безработный");
                throw new ScoringException("Отказ: безработный");
            }
            case SELF_EMPLOYED -> rate = rate.add(BigDecimal.valueOf(2));
            case BUSINESS_OWNER -> rate = rate.add(BigDecimal.ONE);
        }

        switch (emp.getPosition()) {
            case MID_MANAGER -> rate = rate.subtract(BigDecimal.valueOf(2));
            case TOP_MANAGER -> rate = rate.subtract(BigDecimal.valueOf(3));
        }

        if (request.getAmount().compareTo(emp.getSalary().multiply(BigDecimal.valueOf(24))) > 0) {
            log.warn("Скоринг не пройден: сумма превышает 24 зарплаты");
            throw new ScoringException("Отказ: сумма займа превышает 24 зарплаты");
        }

        switch (request.getMaritalStatus()) {
            case MARRIED -> rate = rate.subtract(BigDecimal.valueOf(3));
            case DIVORCED -> rate = rate.add(BigDecimal.ONE);
        }

        int age = Period.between(request.getBirthdate(), LocalDate.now()).getYears();
        if (age < 20 || age > 65) {
            log.warn("Скоринг не пройден: возраст={}", age);
            throw new ScoringException("Отказ: возраст вне диапазона");
        }

        switch (request.getGender()) {
            case FEMALE -> {
                if (age >= 32 && age <= 60) rate = rate.subtract(BigDecimal.valueOf(3));
            }
            case MALE -> {
                if (age >= 30 && age <= 55) rate = rate.subtract(BigDecimal.valueOf(3));
            }
            case NON_BINARY -> rate = rate.add(BigDecimal.valueOf(7));
        }

        if (emp.getWorkExperienceTotal() < 18) {
            log.warn("Скоринг не пройден: общий стаж={}", emp.getWorkExperienceTotal());
            throw new ScoringException("Отказ: общий стаж меньше 18 месяцев");
        }
        if (emp.getWorkExperienceCurrent() < 3) {
            log.warn("Скоринг не пройден: текущий стаж={}", emp.getWorkExperienceCurrent());
            throw new ScoringException("Отказ: текущий стаж меньше 3 месяцев");
        }

        return rate;
    }

    private List<PaymentScheduleElementDto> buildPaymentSchedule(BigDecimal amount, BigDecimal rate, Integer term, BigDecimal monthlyPayment) {
        List<PaymentScheduleElementDto> schedule = new ArrayList<>();
        BigDecimal remainingDebt = amount;
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        LocalDate date = LocalDate.now();

        for (int i = 1; i <= term; i++) {
            date = date.plusMonths(1);
            BigDecimal interestPayment = remainingDebt.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment);
            remainingDebt = remainingDebt.subtract(debtPayment).setScale(2, RoundingMode.HALF_UP);

            schedule.add(PaymentScheduleElementDto.builder()
                    .number(i)
                    .date(date)
                    .totalPayment(monthlyPayment)
                    .interestPayment(interestPayment)
                    .debtPayment(debtPayment)
                    .remainingDebt(remainingDebt)
                    .build());
        }
        return schedule;
    }

}