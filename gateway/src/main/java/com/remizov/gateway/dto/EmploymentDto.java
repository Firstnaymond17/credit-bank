package com.remizov.gateway.dto;


import com.remizov.gateway.dto.enums.EmploymentPosition;
import com.remizov.gateway.dto.enums.EmploymentStatus;
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