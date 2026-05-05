package com.remizov.gateway.controller;

import com.remizov.gateway.client.DealClient;
import com.remizov.gateway.client.StatementClient;
import com.remizov.gateway.dto.FinishRegistrationRequestDto;
import com.remizov.gateway.dto.LoanOfferDto;
import com.remizov.gateway.dto.LoanStatementRequestDto;
import com.remizov.gateway.dto.StatementDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/gateway")
@RequiredArgsConstructor
@Tag(name = "Микросервис Шлюз", description = "методы для обработки кредита")
public class GatewayController {

    private final StatementClient statementClient;
    private final DealClient dealClient;

    @Operation(summary = "Прескоринг и расчёт кредитных предложений")
    @PostMapping("/statement")
    public List<LoanOfferDto> getOffers(@RequestBody LoanStatementRequestDto request) {
        log.info("POST /gateway/statement: amount={}, term={}", request.getAmount(), request.getTerm());
        return statementClient.getOffers(request);
    }

    @Operation(summary = "Выбор кредитного предложения")
    @PostMapping("/statement/offer")
    public void selectOffer(@RequestBody LoanOfferDto offer) {
        log.info("POST /gateway/statement/offer: statementId={}", offer.getStatementId());
        statementClient.selectOffer(offer);
    }

    @Operation(summary = "Завершение регистрации")
    @PostMapping("/calculate/{statementId}")
    public void calculate(@PathVariable String statementId,
                          @RequestBody FinishRegistrationRequestDto request) {
        log.info("POST /gateway/calculate/{}", statementId);
        dealClient.calculate(statementId, request);
    }

    @Operation(summary = "Запрос на отправку документов")
    @PostMapping("/document/{statementId}/send")
    public void sendDocuments(@PathVariable String statementId) {
        log.info("POST /gateway/document/{}/send", statementId);
        dealClient.sendDocuments(statementId);
    }

    @Operation(summary = "Запрос на подписание документов")
    @PostMapping("/document/{statementId}/sign")
    public void signDocuments(@PathVariable String statementId) {
        log.info("POST /gateway/document/{}/sign", statementId);
        dealClient.signDocuments(statementId);
    }

    @Operation(summary = "Подписание документов по коду")
    @PostMapping("/document/{statementId}/code")
    public void codeDocuments(@PathVariable String statementId) {
        log.info("POST /gateway/document/{}/code", statementId);
        dealClient.codeDocuments(statementId);
    }

    @Operation(summary = "Получить заявку по id")
    @GetMapping("/admin/statement/{statementId}")
    public StatementDto getStatement(@PathVariable String statementId) {
        log.info("GET /gateway/admin/statement/{}", statementId);
        return dealClient.getStatement(statementId);
    }

    @Operation(summary = "Получить все заявки")
    @GetMapping("/admin/statement")
    public List<StatementDto> getAllStatements() {
        log.info("GET /gateway/admin/statement");
        return dealClient.getAllStatements();
    }
}