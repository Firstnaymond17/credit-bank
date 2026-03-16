package com.remizov.calculator.service.impl.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MonthlyPaymentUtils {

    public static BigDecimal calculateMonthlyPayment(BigDecimal totalAmount, BigDecimal rate, Integer term) {
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        BigDecimal pow = monthlyRate.add(BigDecimal.ONE).pow(term);
        BigDecimal numerator = monthlyRate.multiply(pow);
        BigDecimal denominator = pow.subtract(BigDecimal.ONE);
        return totalAmount.multiply(numerator.divide(denominator, 10, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
