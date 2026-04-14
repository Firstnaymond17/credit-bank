package com.remizov.statement.dto.validation.annotation;

import com.remizov.statement.dto.validation.validator.NameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NameValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Name {

    String message() default "Имя, Фамилия должны содержать от 2 до 30 латинских букв";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}