package com.remizov.deal.service.impl;

import com.remizov.deal.dto.LoanOfferDto;
import com.remizov.deal.dto.LoanStatementRequestDto;
import com.remizov.deal.entity.Client;
import com.remizov.deal.entity.Statement;
import com.remizov.deal.mapper.ClientMapper;
import com.remizov.deal.mapper.StatementMapper;
import com.remizov.deal.repository.ClientRepository;
import com.remizov.deal.repository.StatementRepository;
import com.remizov.deal.service.StatementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatementServiceImpl implements StatementService {

    private final ClientMapper clientMapper;
    private final StatementMapper statementMapper;
    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;
    private final RestClient restClient;

    @Override
    public List<LoanOfferDto> statement(LoanStatementRequestDto request) {
        log.info("Получен запрос на создание заявки: email={}, amount={}, term={}",
                request.getEmail(), request.getAmount(), request.getTerm());

        Client client = createClient(request);
        clientRepository.save(client);
        log.info("Клиент создан и сохранён: clientId={}", client.getId());

        Statement statement = createStatement(client);
        statementRepository.save(statement);
        log.info("Заявка создана и сохранена: statementId={}", statement.getId());

        log.info("Отправка запроса в калькулятор на /calculator/offers");
        List<LoanOfferDto> offers = restClient.post()
                .uri("/calculator/offers")
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (offers != null) {
            offers.forEach(offer -> offer.setStatementId(statement.getId()));
            log.info("Получено {} офферов от калькулятора, statementId проставлен", offers.size());
        } else {
            log.warn("Калькулятор вернул пустой список офферов");
        }

        return offers;
    }

    private Client createClient(LoanStatementRequestDto request) {
        log.debug("Создание клиента: firstName={}, lastName={}", request.getFirstname(), request.getLastname());
        return clientMapper.toClient(request);
    }

    private Statement createStatement(Client client) {
        log.debug("Создание заявки для клиента: clientId={}", client.getId());
        return statementMapper.toStatement(client);
    }
}