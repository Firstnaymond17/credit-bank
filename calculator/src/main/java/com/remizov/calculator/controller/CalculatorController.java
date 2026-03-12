package com.remizov.calculator.controller;

import com.remizov.calculator.dto.CreditDto;
import com.remizov.calculator.dto.LoanOfferDto;
import com.remizov.calculator.dto.LoanStatementRequestDto;
import com.remizov.calculator.dto.ScoringDataDto;
import com.remizov.calculator.service.CalculatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping("/calculator")
@RequiredArgsConstructor
@Tag(name = "Микросервис Калькулятор", description = "Методы для работы расчета условий кредита")
public class CalculatorController {

    private final CalculatorService calculatorService;

    @Operation(summary = "Отправить данные о клиенте", description = "Производится прескоринг входных данных, на выход клиент получает 4 предложения")
    @PostMapping("/offers")
    public List<LoanOfferDto> createOffers(@Valid @RequestBody LoanStatementRequestDto request) {
        return calculatorService.createOffers(request);
    }

    @Operation(summary = "Отправить данные о клиенте", description = "МС Калькулятор рассчитывает все данные по кредиту, на выход клиент получает условия кредита")
    @PostMapping("/calc")
    public CreditDto createCredit(@Valid @RequestBody ScoringDataDto scoringData) {
        return calculatorService.createCredit(scoringData);
    }

}
