package com.remizov.calculator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ScoringException.class)
    public ResponseEntity<Map<String, String>> handleScoringException(ScoringException e) {
        return ResponseEntity.status(422).body(Map.of("error", e.getMessage()));
    }
}