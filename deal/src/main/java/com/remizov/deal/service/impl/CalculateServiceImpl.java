package com.remizov.deal.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.remizov.deal.dto.*;
import com.remizov.deal.entity.Client;
import com.remizov.deal.entity.Credit;
import com.remizov.deal.entity.Statement;
import com.remizov.deal.enums.ApplicationStatus;
import com.remizov.deal.mapper.CreditMapper;
import com.remizov.deal.mapper.ScoringDataMapper;
import com.remizov.deal.repository.CreditRepository;
import com.remizov.deal.repository.StatementRepository;
import com.remizov.deal.service.CalculateService;
import com.remizov.deal.utils.StatementUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculateServiceImpl implements CalculateService {

    private final ScoringDataMapper scoringDataMapper;
    private final CreditMapper creditMapper;
    private final StatementRepository statementRepository;
    private final CreditRepository creditRepository;
    private final RestClient restClient;

    @Override
    public void calculate(String statementId, FinishRegistrationRequestDto request) {
        log.info("Получен запрос на завершение регистрации: statementId={}", statementId);

        Statement statement = statementRepository.findById(UUID.fromString(statementId))
                .orElseThrow(() -> new RuntimeException("Заявка не найдена: " + statementId));
        log.info("Заявка найдена: statementId={}", statement.getId());

        Client client = statement.getClient();
        log.info("Клиент получен из заявки: clientId={}", client.getId());

        ObjectMapper mapper = new ObjectMapper();
        LoanOfferDto appliedOffer = mapper.convertValue(statement.getAppliedOffer(), LoanOfferDto.class);
        log.debug("Выбранный оффер: rate={}, term={}, isInsuranceEnabled={}",
                appliedOffer.getRate(), appliedOffer.getTerm(), appliedOffer.getIsInsuranceEnabled());

        ScoringDataDto scoringData = createScoringData(client, appliedOffer, request);
        log.info("Скоринг данные сформированы, отправка в калькулятор на /calculator/calc");

        CreditDto creditDto = restClient.post()
                .uri("/calculator/calc")
                .body(scoringData)
                .retrieve()
                .body(CreditDto.class);

        if (creditDto == null) {
            log.error("Калькулятор не вернул данные по кредиту: statementId={}", statementId);
            throw new RuntimeException("Калькулятор не вернул данные по кредиту");
        }
        log.info("Получены данные по кредиту от калькулятора: amount={}, rate={}, monthlyPayment={}",
                creditDto.getAmount(), creditDto.getRate(), creditDto.getMonthlyPayment());

        Credit credit = createCredit(creditDto);
        creditRepository.save(credit);
        log.info("Кредит создан и сохранён: creditId={}", credit.getId());

        StatementUtils.updateStatementStatus(statement, ApplicationStatus.CC_APPROVED);
        log.info("Статус заявки обновлён: statementId={}, status={}", statement.getId(), ApplicationStatus.CC_APPROVED);

        statement.setCredit(credit);
        statementRepository.save(statement);
        log.info("Заявка обновлена и сохранена: statementId={}", statement.getId());
    }

    private ScoringDataDto createScoringData(Client client, LoanOfferDto appliedOffer, FinishRegistrationRequestDto request) {
        log.debug("Создание ScoringDataDto: clientId={}", client.getId());
        return scoringDataMapper.toScoringData(client, appliedOffer, request);
    }

    private Credit createCredit(CreditDto creditDto) {
        log.debug("Создание Credit из CreditDto: amount={}, term={}", creditDto.getAmount(), creditDto.getTerm());
        return creditMapper.toCredit(creditDto);
    }
}