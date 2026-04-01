package com.remizov.deal.controller;

import com.remizov.deal.dto.FinishRegistrationRequestDto;
import com.remizov.deal.dto.LoanOfferDto;
import com.remizov.deal.dto.LoanStatementRequestDto;
import com.remizov.deal.service.CalculateService;
import com.remizov.deal.service.OfferService;
import com.remizov.deal.service.StatementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
@Tag(name = "Микросервис Сделка", description = "Методы для работы с кредитными заявками")
public class DealController {

    private final StatementService statementService;
    private final OfferService offerService;
    private final CalculateService calculateService;

    @Operation(
            summary = "Расчёт возможных условий кредита",
            description = "Создаёт клиента и заявку в БД, отправляет запрос в калькулятор и возвращает список из 4 кредитных предложений"
    )
    @PostMapping("/statement")
    public ResponseEntity<List<LoanOfferDto>> statement(@Valid @RequestBody LoanStatementRequestDto request) {
        List<LoanOfferDto> offers = statementService.statement(request);
        log.info("POST /deal/statement: возвращено {} офферов", offers != null ? offers.size() : 0);
        return ResponseEntity.ok(offers);
    }

    @Operation(
            summary = "Выбор одного из предложений",
            description = "Обновляет статус заявки, сохраняет выбранное предложение в поле appliedOffer"
    )
    @PostMapping("/offer/select")
    public ResponseEntity<Void> selectOffer(@RequestBody LoanOfferDto request) {
        offerService.selectOffer(request);
        log.info("POST /deal/offer/select: оффер выбран успешно, statementId={}", request.getStatementId());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Завершение регистрации и полный подсчёт кредита",
            description = " Добавляет в ScoringDataDto данные клиента, отправляет в калькулятор, создаёт кредит и обновляет заявку"
    )
    @PostMapping("/calculate/{statementId}")
    public ResponseEntity<Void> calculate(
            @Parameter(description = "ID заявки") @PathVariable String statementId,
            @RequestBody FinishRegistrationRequestDto request) {
        calculateService.calculate(statementId, request);
        log.info("POST /deal/calculate/{}: расчёт завершён успешно", statementId);
        return ResponseEntity.ok().build();
    }
}