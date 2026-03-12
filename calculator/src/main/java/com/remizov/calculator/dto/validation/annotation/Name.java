package com.remizov.calculator.dto.validation.annotation;

import com.remizov.calculator.dto.validation.validator.NameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = NameValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Name {

    String message() default "Имя, Фамилия должны содержать от 2 до 30 латинских букв";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}