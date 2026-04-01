package com.remizov.deal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.remizov.deal.dto.*;
import com.remizov.deal.entity.Client;
import com.remizov.deal.entity.Credit;
import com.remizov.deal.entity.Passport;
import com.remizov.deal.entity.Statement;
import com.remizov.deal.enums.ApplicationStatus;
import com.remizov.deal.enums.CreditStatus;
import com.remizov.deal.mapper.CreditMapper;
import com.remizov.deal.mapper.ScoringDataMapper;
import com.remizov.deal.repository.CreditRepository;
import com.remizov.deal.repository.StatementRepository;
import com.remizov.deal.service.impl.CalculateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CalculateServiceTest {

    @Mock
    private CreditMapper creditMapper;

    @Mock
    private ScoringDataMapper scoringDataMapper;

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private CreditRepository creditRepository;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private CalculateServiceImpl calculateService;

    private FinishRegistrationRequestDto request;
    private CreditDto creditDto;

    @BeforeEach
    void setUp() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        File requestFile = ResourceUtils.getFile("classpath:test-data/finish-registration-request.json");
        request = objectMapper.readValue(requestFile, FinishRegistrationRequestDto.class);

        File creditFile = ResourceUtils.getFile("classpath:test-data/credit-dto.json");
        creditDto = objectMapper.readValue(creditFile, CreditDto.class);

        lenient().when(scoringDataMapper.toScoringData(any(), any(), any()))
                .thenReturn(new ScoringDataDto());

        lenient().when(creditMapper.toCredit(any())).thenAnswer(invocation -> {
            CreditDto dto = invocation.getArgument(0);
            Credit credit = new Credit();
            credit.setAmount(dto.getAmount());
            credit.setTerm(dto.getTerm());
            credit.setRate(dto.getRate());
            credit.setCreditStatus(CreditStatus.CALCULATED);
            return credit;
        });
    }

    @Test
    @DisplayName("calculate — кредит создаётся и сохраняется в БД")
    void calculate_creditIsSaved() {
        UUID statementId = UUID.randomUUID();
        mockRestClient(creditDto);
        when(statementRepository.findById(statementId)).thenReturn(Optional.of(statement(statementId)));

        calculateService.calculate(statementId.toString(), request);

        verify(creditRepository).save(any(Credit.class));
    }

    @Test
    @DisplayName("calculate — заявка обновляется и сохраняется в БД")
    void calculate_statementIsSaved() {
        UUID statementId = UUID.randomUUID();
        mockRestClient(creditDto);
        when(statementRepository.findById(statementId)).thenReturn(Optional.of(statement(statementId)));

        calculateService.calculate(statementId.toString(), request);

        verify(statementRepository).save(any(Statement.class));
    }

    @Test
    @DisplayName("calculate — статус заявки обновляется на CC_APPROVED")
    void calculate_statementStatusUpdatedToCcApproved() {
        UUID statementId = UUID.randomUUID();
        Statement statement = statement(statementId);
        mockRestClient(creditDto);
        when(statementRepository.findById(statementId)).thenReturn(Optional.of(statement));

        calculateService.calculate(statementId.toString(), request);

        assertEquals(ApplicationStatus.CC_APPROVED, statement.getStatus());
    }

    @Test
    @DisplayName("calculate — кредит создаётся с правильными полями")
    void calculate_creditHasCorrectFields() {
        UUID statementId = UUID.randomUUID();
        mockRestClient(creditDto);
        when(statementRepository.findById(statementId)).thenReturn(Optional.of(statement(statementId)));

        calculateService.calculate(statementId.toString(), request);

        ArgumentCaptor<Credit> captor = ArgumentCaptor.forClass(Credit.class);
        verify(creditRepository).save(captor.capture());
        Credit credit = captor.getValue();

        assertAll(
                () -> assertEquals(BigDecimal.valueOf(100000), credit.getAmount()),
                () -> assertEquals(12, credit.getTerm()),
                () -> assertEquals(BigDecimal.valueOf(21), credit.getRate()),
                () -> assertEquals(CreditStatus.CALCULATED, credit.getCreditStatus())
        );
    }

    @Test
    @DisplayName("calculate — выбрасывает исключение если заявка не найдена")
    void calculate_throwsExceptionWhenStatementNotFound() {
        UUID statementId = UUID.randomUUID();
        when(statementRepository.findById(statementId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> calculateService.calculate(statementId.toString(), request));

        assertEquals("Заявка не найдена: " + statementId, exception.getMessage());
    }

    @Test
    @DisplayName("calculate — выбрасывает исключение если калькулятор вернул null")
    void calculate_throwsExceptionWhenCalculatorReturnsNull() {
        UUID statementId = UUID.randomUUID();
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Object.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CreditDto.class)).thenReturn(null);
        when(statementRepository.findById(statementId)).thenReturn(Optional.of(statement(statementId)));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> calculateService.calculate(statementId.toString(), request));

        assertEquals("Калькулятор не вернул данные по кредиту", exception.getMessage());
    }

    private void mockRestClient(CreditDto response) {
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Object.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CreditDto.class)).thenReturn(response);
    }

    private static Statement statement(UUID id) {
        Passport passport = new Passport();
        passport.setSeries("1234");
        passport.setNumber("123456");

        Client client = new Client();
        client.setFirstName("Ivan");
        client.setLastName("Ivanov");
        client.setBirthDate(LocalDate.of(1990, 1, 1));
        client.setPassport(passport);

        Statement statement = new Statement();
        statement.setId(id);
        statement.setClient(client);
        statement.setStatusHistory(new ArrayList<>());
        statement.setAppliedOffer(LoanOfferDto.builder()
                .statementId(id)
                .requestedAmount(BigDecimal.valueOf(100000))
                .term(12)
                .rate(BigDecimal.valueOf(21))
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .build());
        return statement;
    }
}