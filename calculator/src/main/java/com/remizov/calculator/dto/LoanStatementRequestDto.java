package com.remizov.calculator.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanStatementRequestDto {
    private BigDecimal amount;
    private Integer term;
    private String firstname;
    private String middleName;
    private String email;
    private LocalDate birthDate;
    private String passportSeries;
    private String passportNumber;
}
