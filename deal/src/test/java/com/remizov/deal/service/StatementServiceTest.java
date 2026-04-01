package com.remizov.deal.service;

import com.remizov.deal.dto.LoanOfferDto;
import com.remizov.deal.dto.LoanStatementRequestDto;
import com.remizov.deal.entity.Client;
import com.remizov.deal.entity.Passport;
import com.remizov.deal.entity.Statement;
import com.remizov.deal.enums.ApplicationStatus;
import com.remizov.deal.mapper.ClientMapper;
import com.remizov.deal.mapper.StatementMapper;
import com.remizov.deal.repository.ClientRepository;
import com.remizov.deal.repository.StatementRepository;
import com.remizov.deal.service.impl.StatementServiceImpl;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatementServiceTest {

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private StatementMapper statementMapper;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private StatementServiceImpl statementService;

    private static final EasyRandom easyRandom = new EasyRandom();

    @BeforeEach
    void setUp() {
        when(clientMapper.toClient(any())).thenAnswer(invocation -> {
            LoanStatementRequestDto req = invocation.getArgument(0);
            Passport passport = new Passport();
            passport.setSeries(req.getPassportSeries());
            passport.setNumber(req.getPassportNumber());
            Client client = new Client();
            client.setFirstName(req.getFirstname());
            client.setLastName(req.getLastname());
            client.setEmail(req.getEmail());
            client.setPassport(passport);
            return client;
        });

        when(statementMapper.toStatement(any())).thenAnswer(invocation -> {
            Statement statement = new Statement();
            statement.setStatus(ApplicationStatus.PREAPPROVAL);
            statement.setCreationDate(LocalDateTime.now());
            statement.setStatusHistory(new ArrayList<>());
            return statement;
        });
    }

    @Test
    @DisplayName("statement — клиент и заявка сохраняются в БД")
    void statement_savesClientAndStatement() {
        mockRestClient(List.of(easyRandom.nextObject(LoanOfferDto.class)));

        statementService.statement(easyRandom.nextObject(LoanStatementRequestDto.class));

        verify(clientRepository).save(any(Client.class));
        verify(statementRepository).save(any(Statement.class));
    }

    @Test
    @DisplayName("statement — каждому офферу проставляется statementId")
    void statement_setsStatementIdToOffers() {
        UUID statementId = UUID.randomUUID();
        mockRestClient(List.of(easyRandom.nextObject(LoanOfferDto.class)));
        doAnswer(invocation -> {
            Statement s = invocation.getArgument(0);
            s.setId(statementId);
            return null;
        }).when(statementRepository).save(any(Statement.class));

        List<LoanOfferDto> result = statementService.statement(easyRandom.nextObject(LoanStatementRequestDto.class));

        assertEquals(statementId, result.getFirst().getStatementId());
    }

    @Test
    @DisplayName("statement — клиент создаётся с корректными полями")
    void statement_clientHasCorrectFields() {
        LoanStatementRequestDto request = easyRandom.nextObject(LoanStatementRequestDto.class);
        mockRestClient(List.of());

        statementService.statement(request);

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(captor.capture());
        Client client = captor.getValue();

        assertAll(
                () -> assertEquals(request.getFirstname(), client.getFirstName()),
                () -> assertEquals(request.getLastname(), client.getLastName()),
                () -> assertEquals(request.getEmail(), client.getEmail()),
                () -> assertEquals(request.getPassportSeries(), client.getPassport().getSeries()),
                () -> assertEquals(request.getPassportNumber(), client.getPassport().getNumber())
        );
    }

    @Test
    @DisplayName("statement — заявка создаётся со статусом PREAPPROVAL")
    void statement_statementHasPreapprovalStatus() {
        mockRestClient(List.of());

        statementService.statement(easyRandom.nextObject(LoanStatementRequestDto.class));

        ArgumentCaptor<Statement> captor = ArgumentCaptor.forClass(Statement.class);
        verify(statementRepository).save(captor.capture());
        Statement statement = captor.getValue();

        assertAll(
                () -> assertEquals(ApplicationStatus.PREAPPROVAL, statement.getStatus()),
                () -> assertNotNull(statement.getCreationDate()),
                () -> assertTrue(statement.getStatusHistory().isEmpty())
        );
    }

    @SuppressWarnings("unchecked")
    private void mockRestClient(List<LoanOfferDto> response) {
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Object.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(response);
    }
}