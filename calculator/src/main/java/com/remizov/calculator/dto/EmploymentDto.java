package com.remizov.calculator.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class EmploymentDto {
    private EmploymentStatus employmentStatus;

    private String employerINN;

    private BigDecimal salary;

    private Position position;

    private Integer workExperienceTotal;

    private Integer workExperienceCurrent;

    public enum EmploymentStatus {
        UNEMPLOYED,
        EMPLOYED,
        SELF_EMPLOYED,
        BUSINESS_OWNER
    }

    public enum Position {
        MID_MANAGER,
        TOP_MANAGER,
    }
}

