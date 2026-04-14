package com.remizov.statement.dto;

import com.remizov.statement.dto.validation.annotation.Adulthood;
import com.remizov.statement.dto.validation.annotation.Name;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.*;

@Data
public class LoanStatementRequestDto {

    @NotNull
    @DecimalMin(value = "15000", message = "Сумма кредита должна быть не меньше 15000")
    private BigDecimal amount;

    @NotNull
    @Min(value = 3, message = "Срок кредита должен быть от 3 месяцев")
    private Integer term;

    @NotBlank
    @Name
    private String firstname;

    @NotBlank
    @Name
    private String lastname;

    @Name
    private String middleName;

    @NotBlank
    @Pattern(
            regexp = "^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$",
            message = "Неверный формат email"
    )
    private String email;

    @NotNull
    @Adulthood
    private LocalDate birthdate;

    @Pattern(regexp = "\\d{4}", message = "Неверный формат ввода")
    private String passportSeries;

    @Pattern(regexp = "\\d{6}", message = "Неверный формат ввода")
    private String passportNumber;
}
