package com.remizov.calculator.dto;

import com.remizov.calculator.dto.validation.annotation.Adulthood;
import com.remizov.calculator.dto.validation.annotation.Name;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ScoringDataDto {
    @NotNull
    @DecimalMin(value = "15000", message = "Loan amount must be at least 15000")
    private BigDecimal amount;

    @NotNull
    @Min(value = 3, message = "Loan term must be at least 3 months")
    private Integer term;

    @NotBlank
    @Name
    private String firstName;

    @NotBlank
    @Name
    private String lastName;

    @Name
    private String middleName;

    private Gender gender;

    @NotNull
    @Adulthood
    private LocalDate birthdate;

    @Pattern(regexp = "\\d{4}", message = "Passport series must be 4 digits")
    private String passportSeries;

    @Pattern(regexp = "\\d{6}", message = "Passport number must be 6 digits")
    private String passportNumber;

    private LocalDate passportIssueDate;

    private String passportIssueBranch;

    private MaritalStatus maritalStatus;

    private Integer dependentAmount;

    private EmploymentDto employment;

    private String accountNumber;

    private Boolean isInsuranceEnabled;

    private Boolean isSalaryClient;

    public enum Gender {
        MALE, FEMALE, NON_BINARY
    }

    public enum MaritalStatus {
        MARRIED, DIVORCED
    }

}