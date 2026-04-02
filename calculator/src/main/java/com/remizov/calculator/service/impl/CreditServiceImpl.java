package com.remizov.calculator.service.impl;

import com.remizov.calculator.dto.*;
import com.remizov.calculator.exception.ScoringException;
import com.remizov.calculator.service.CreditService;
import com.remizov.calculator.utils.MonthlyPaymentUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.remizov.calculator.properties.ScoringProperties;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {

    private final ScoringProperties scoringProperties;

    @Override
    public CreditDto createCredit(ScoringDataDto request) {
        log.info("Получен запрос на расчёт кредита: amount={}, term={}", request.getAmount(), request.getTerm());

        BigDecimal rate = BigDecimal.valueOf(scoringProperties.getBaseRate());
        rate = scoring(request, rate);
        log.debug("Итоговая ставка после скоринга: rate={}", rate);

        BigDecimal monthlyPayment = MonthlyPaymentUtils.calculateMonthlyPayment(request.getAmount(), rate, request.getTerm());
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
        if (request.getIsInsuranceEnabled()) rate = rate.subtract(BigDecimal.valueOf(scoringProperties.getInsuranceRateDiscount()));
        if (request.getIsSalaryClient()) rate = rate.subtract(BigDecimal.valueOf(scoringProperties.getSalaryClientRateDiscount()));

        EmploymentDto emp = request.getEmployment();

        switch (emp.getEmploymentStatus()) {
            case UNEMPLOYED -> {
                log.warn("Скоринг не пройден: безработный");
                throw new ScoringException("Отказ: безработный");
            }
            case SELF_EMPLOYED -> rate = rate.add(BigDecimal.valueOf(scoringProperties.getSelfEmployedRateIncrease()));
            case BUSINESS_OWNER -> rate = rate.add(BigDecimal.valueOf(scoringProperties.getBusinessOwnerRateIncrease()));
        }

        switch (emp.getPosition()) {
            case MID_MANAGER -> rate = rate.subtract(BigDecimal.valueOf(scoringProperties.getMidManagerRateDiscount()));
            case TOP_MANAGER -> rate = rate.subtract(BigDecimal.valueOf(scoringProperties.getTopManagerRateDiscount()));
        }

        if (request.getAmount().compareTo(emp.getSalary().multiply(BigDecimal.valueOf(scoringProperties.getMaxSalaryMultiplier()))) > 0) {
            log.warn("Скоринг не пройден: сумма превышает 24 зарплаты");
            throw new ScoringException("Отказ: сумма займа превышает 24 зарплаты");
        }

        switch (request.getMaritalStatus()) {
            case MARRIED -> rate = rate.subtract(BigDecimal.valueOf(scoringProperties.getMarriedRateDiscount()));
            case DIVORCED -> rate = rate.add(BigDecimal.valueOf(scoringProperties.getDivorcedRateIncrease()));
        }

        int age = Period.between(request.getBirthdate(), LocalDate.now()).getYears();
        if (age < scoringProperties.getMinPossibleAge() || age > scoringProperties.getMaxPossibleAge()) {
            log.warn("Скоринг не пройден: возраст={}", age);
            throw new ScoringException("Отказ: возраст вне диапазона");
        }

        if (emp.getWorkExperienceTotal() < scoringProperties.getMinWorkExperienceTotal()) {
            log.warn("Скоринг не пройден: общий стаж={}", emp.getWorkExperienceTotal());
            throw new ScoringException("Отказ: общий стаж меньше 18 месяцев");
        }
        if (emp.getWorkExperienceCurrent() < scoringProperties.getMinWorkExperienceCurrent()) {
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