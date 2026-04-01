package com.remizov.deal.dto;

import com.remizov.deal.enums.EmploymentPosition;
import com.remizov.deal.enums.EmploymentStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EmploymentDto {

    private EmploymentStatus employmentStatus;

    private String employerInn;

    private BigDecimal salary;

    private EmploymentPosition position;

    private Integer workExperienceTotal;

    private Integer workExperienceCurrent;
}