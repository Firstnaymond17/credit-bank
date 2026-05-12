package com.remizov.gateway.dto.validation.validator;

import com.remizov.gateway.dto.validation.annotation.Name;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<Name, String> {

    private static final String NAME_PATTERN =
            "^[A-Za-zА-Яа-я\\-]{2,30}$";


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.matches(NAME_PATTERN);
    }
}