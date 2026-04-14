package com.remizov.statement.dto.validation.annotation;

import com.remizov.statement.dto.validation.validator.AdulthoodValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AdulthoodValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Adulthood {

    String message() default "Клиент должен быть старше 18 лет";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}