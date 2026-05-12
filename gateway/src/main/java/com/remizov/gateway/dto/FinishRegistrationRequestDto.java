package com.remizov.gateway.dto;


import com.remizov.gateway.dto.enums.Gender;
import com.remizov.gateway.dto.enums.MaritalStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FinishRegistrationRequestDto {

    private Gender gender;

    private MaritalStatus maritalStatus;

    private Integer dependentAmount;

    private LocalDate passportIssueDate;

    private String passportIssueBranch;

    private EmploymentDto employment;

    private String accountNumber;
}