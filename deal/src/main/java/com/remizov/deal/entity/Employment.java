package com.remizov.deal.entity;

import com.remizov.deal.entity.enums.EmploymentPosition;
import com.remizov.deal.entity.enums.EmploymentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
public class Employment {

    private EmploymentStatus status;

    private String employerInn;

    private BigDecimal salary;

    private EmploymentPosition position;

    private Integer workExperienceTotal;

    private Integer workExperienceCurrent;
}