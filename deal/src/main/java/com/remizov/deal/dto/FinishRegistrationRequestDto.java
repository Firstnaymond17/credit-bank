package com.remizov.deal.dto;

import com.remizov.deal.enums.Gender;
import com.remizov.deal.enums.MaritalStatus;
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