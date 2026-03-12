package com.remizov.calculator.dto;

import lombok.Builder;
import lombok.Data;
import org.apache.tomcat.util.json.JSONParser;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
public class PaymentScheduleElementDto {
    private Integer number;

    private LocalDate date;

    private BigDecimal totalPayment;

    private BigDecimal interestPayment;

    private BigDecimal debtPayment;

    private BigDecimal remainingDebt;

}
