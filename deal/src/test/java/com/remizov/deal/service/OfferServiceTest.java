package com.remizov.deal.service;

import com.remizov.deal.dto.LoanOfferDto;
import com.remizov.deal.entity.Statement;
import com.remizov.deal.enums.ApplicationStatus;
import com.remizov.deal.repository.StatementRepository;
import com.remizov.deal.service.impl.OfferServiceImpl;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OfferServiceTest {

    @Mock
    private StatementRepository statementRepository;

    @InjectMocks
    private OfferServiceImpl offerService;

    private static final EasyRandom easyRandom = new EasyRandom();

    @Test
    @DisplayName("selectOffer — статус заявки обновляется на APPROVED")
    void selectOffer_updatesStatementStatusToApproved() {
        UUID statementId = UUID.randomUUID();
        Statement statement = statement(statementId);
        LoanOfferDto offer = offer(statementId);
        when(statementRepository.findById(statementId)).thenReturn(Optional.of(statement));

        offerService.selectOffer(offer);

        assertEquals(ApplicationStatus.APPROVED, statement.getStatus());
    }

    @Test
    @DisplayName("selectOffer — выбранный оффер сохраняется в appliedOffer")
    void selectOffer_setsAppliedOffer() {
        UUID statementId = UUID.randomUUID();
        Statement statement = statement(statementId);
        LoanOfferDto offer = offer(statementId);
        when(statementRepository.findById(statementId)).thenReturn(Optional.of(statement));

        offerService.selectOffer(offer);

        assertEquals(offer, statement.getAppliedOffer());
    }

    @Test
    @DisplayName("selectOffer — заявка сохраняется в БД")
    void selectOffer_savesStatement() {
        UUID statementId = UUID.randomUUID();
        Statement statement = statement(statementId);
        when(statementRepository.findById(statementId)).thenReturn(Optional.of(statement));

        offerService.selectOffer(offer(statementId));

        verify(statementRepository).save(statement);
    }

    @Test
    @DisplayName("selectOffer — история статусов обновляется")
    void selectOffer_updatesStatusHistory() {
        UUID statementId = UUID.randomUUID();
        Statement statement = statement(statementId);
        when(statementRepository.findById(statementId)).thenReturn(Optional.of(statement));

        offerService.selectOffer(offer(statementId));

        assertFalse(statement.getStatusHistory().isEmpty());
    }

    @Test
    @DisplayName("selectOffer — выбрасывает исключение если заявка не найдена")
    void selectOffer_throwsExceptionWhenStatementNotFound() {
        UUID statementId = UUID.randomUUID();
        when(statementRepository.findById(statementId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> offerService.selectOffer(offer(statementId)));

        assertEquals("Заявка не найдена " + statementId, exception.getMessage());
    }

    private static Statement statement(UUID id) {
        Statement statement = new Statement();
        statement.setId(id);
        statement.setStatusHistory(new ArrayList<>());
        return statement;
    }

    private LoanOfferDto offer(UUID statementId) {
        LoanOfferDto offer = easyRandom.nextObject(LoanOfferDto.class);
        offer.setStatementId(statementId);
        return offer;
    }
}