package com.remizov.calculator.dto.validation.annotation;

import com.remizov.calculator.dto.validation.validator.AdulthoodValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = AdulthoodValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Adulthood {

    String message() default "Клиент должен быть старше 18 лет";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}