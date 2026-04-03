package com.remizov.deal.service;

import com.remizov.deal.client.CalculatorClient;
import com.remizov.deal.dto.*;
import com.remizov.deal.entity.Credit;
import com.remizov.deal.entity.Statement;
import com.remizov.deal.entity.enums.ApplicationStatus;
import com.remizov.deal.entity.enums.CreditStatus;
import com.remizov.deal.mapper.CreditMapper;
import com.remizov.deal.mapper.ScoringDataMapper;
import com.remizov.deal.repository.CreditRepository;
import com.remizov.deal.repository.StatementRepository;
import com.remizov.deal.service.impl.CalculateServiceImpl;

import com.remizov.deal.utils.JsonTestDataUtils;
import com.remizov.deal.utils.TestDataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CalculateServiceTest {

    @Mock
    private CalculatorClient calculatorClient;

    @Mock
    private CreditMapper creditMapper;

    @Mock
    private ScoringDataMapper scoringDataMapper;

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private CreditRepository creditRepository;


    @InjectMocks
    private CalculateServiceImpl calculateService;

    private FinishRegistrationRequestDto request;
    private CreditDto creditDto;

    @BeforeEach
    void setUp() {

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
    @DisplayName("calculate — сохраняет кредит, заявку и обновляет статус")
    void calculate_savesAll() throws Exception {
        UUID statementId = UUID.randomUUID();
        Statement statement = statement(statementId);
        FinishRegistrationRequestDto request = loadFinishRegistrationRequest();
        CreditDto creditDto = loadCreditDto();

        when(calculatorClient.calculateCredit(any(ScoringDataDto.class))).thenReturn(creditDto);
        when(statementRepository.findById(statementId)).thenReturn(Optional.of(statement));

        calculateService.calculate(statementId.toString(), request);

        verify(creditRepository).save(any(Credit.class));
        verify(statementRepository).save(statement);
        assertEquals(ApplicationStatus.CC_APPROVED, statement.getStatus());
    }

    @Test
    @DisplayName("calculate — кредит создаётся с правильными полями")
    void calculate_creditHasCorrectFields() throws Exception {
        UUID statementId = UUID.randomUUID();
        FinishRegistrationRequestDto request = loadFinishRegistrationRequest();
        CreditDto creditDto = loadCreditDto();

        when(calculatorClient.calculateCredit(any(ScoringDataDto.class))).thenReturn(creditDto);
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

        when(calculatorClient.calculateCredit(any(ScoringDataDto.class))).thenReturn(null);
        when(statementRepository.findById(statementId)).thenReturn(Optional.of(statement(statementId)));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> calculateService.calculate(statementId.toString(), request));

        assertEquals("Калькулятор не вернул данные по кредиту", exception.getMessage());
        verify(calculatorClient).calculateCredit(any(ScoringDataDto.class));
    }

    private static Statement statement(UUID id) {
        return TestDataUtils.createStatement(id);
    }

    private FinishRegistrationRequestDto loadFinishRegistrationRequest() throws Exception {
        return JsonTestDataUtils.read("test-data/finish-registration-request.json", FinishRegistrationRequestDto.class);
    }

    private CreditDto loadCreditDto() throws Exception {
        return JsonTestDataUtils.read("test-data/credit-dto.json", CreditDto.class);
    }
}