package com.remizov.statement.dto.validation.validator;

import com.remizov.statement.dto.validation.annotation.Adulthood;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class AdulthoodValidator implements ConstraintValidator<Adulthood, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        return Period.between(value, LocalDate.now()).getYears() >= 18;
    }
}