package com.remizov.calculator.controller;

import com.remizov.calculator.dto.CreditDto;
import com.remizov.calculator.dto.LoanOfferDto;
import com.remizov.calculator.dto.LoanStatementRequestDto;
import com.remizov.calculator.dto.ScoringDataDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/calculator")
public class CalculatorController {


    @PostMapping("/offers")
    public List<LoanOfferDto> calculateCondition(LoanStatementRequestDto loanStatementRequestDto) {

        return List.of();
    }

    @PostMapping("/calc")
    public CreditDto calculateParameters(ScoringDataDto scoringDataDto) {

        return new CreditDto();
    }


}
