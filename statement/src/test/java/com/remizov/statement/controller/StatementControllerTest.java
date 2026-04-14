package com.remizov.statement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.remizov.statement.dto.LoanOfferDto;
import com.remizov.statement.dto.LoanStatementRequestDto;
import com.remizov.statement.service.OfferService;
import com.remizov.statement.service.StatementService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatementController.class)
class StatementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatementService statementService;

    @MockitoBean
    private OfferService offerService;

    private ObjectMapper objectMapper;
    private static final EasyRandom easyRandom = new EasyRandom();

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("POST /statement — успешный запрос, возвращает 4 оффера")
    void getOffers_validRequest_returns200WithOffers() throws Exception {
        List<LoanOfferDto> offers = List.of(
                offerDto(false, false, BigDecimal.valueOf(25)),
                offerDto(false, true, BigDecimal.valueOf(24)),
                offerDto(true, false, BigDecimal.valueOf(22)),
                offerDto(true, true, BigDecimal.valueOf(21))
        );
        when(statementService.getOffers(any())).thenReturn(offers);

        mockMvc.perform(post("/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statementRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].isInsuranceEnabled").value(false))
                .andExpect(jsonPath("$[2].isInsuranceEnabled").value(true));

        verify(statementService, times(1)).getOffers(any());
    }

    @Test
    @DisplayName("POST /statement — пустое тело запроса → 400")
    void getOffers_emptyBody_returns400() throws Exception {
        mockMvc.perform(post("/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(statementService);
    }

    @Test
    @DisplayName("POST /statement/offer — успешный запрос → 200")
    void selectOffer_validRequest_returns200() throws Exception {
        doNothing().when(offerService).selectOffer(any());

        mockMvc.perform(post("/statement/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(easyRandom.nextObject(LoanOfferDto.class))))
                .andExpect(status().isOk());

        verify(offerService, times(1)).selectOffer(any());
    }

    private static LoanStatementRequestDto statementRequest() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setEmail("ivan@example.com");
        request.setPassportSeries("1234");
        request.setPassportNumber("123456");
        request.setAmount(BigDecimal.valueOf(100000));
        request.setTerm(12);
        request.setBirthdate(LocalDate.of(1990, 1, 1));
        request.setFirstname("Ivan");
        request.setLastname("Ivanov");
        request.setMiddleName("Ivanovich");
        return request;
    }

    private static LoanOfferDto offerDto(boolean insurance, boolean salary, BigDecimal rate) {
        LoanOfferDto offer = easyRandom.nextObject(LoanOfferDto.class);
        offer.setIsInsuranceEnabled(insurance);
        offer.setIsSalaryClient(salary);
        offer.setRate(rate);
        return offer;
    }
}