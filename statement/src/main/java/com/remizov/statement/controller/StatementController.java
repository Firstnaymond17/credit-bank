package com.remizov.statement.controller;

import com.remizov.statement.dto.LoanOfferDto;
import com.remizov.statement.dto.LoanStatementRequestDto;
import com.remizov.statement.service.OfferService;
import com.remizov.statement.service.StatementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/statement")
@RequiredArgsConstructor
@Tag(name = "Микросервис Заявка", description = "Методы для работы с кредитными заявками")
public class StatementController {

    private final StatementService statementService;
    private final OfferService offerService;

    @Operation(summary = "Прескоринг и расчёт кредитных предложений")
    @PostMapping
    public List<LoanOfferDto> getOffers(@Valid @RequestBody LoanStatementRequestDto request) {
        log.info("POST /statement: amount={}, term={}", request.getAmount(), request.getTerm());
        return statementService.getOffers(request);
    }

    @Operation(summary = "Выбор кредитного предложения")
    @PostMapping("/offer")
    public void selectOffer(@RequestBody LoanOfferDto offer) {
        log.info("POST /statement/offer: statementId={}", offer.getStatementId());
        offerService.selectOffer(offer);
    }
}