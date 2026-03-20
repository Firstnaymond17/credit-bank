package com.remizov.calculator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.remizov.calculator.dto.*;
import com.remizov.calculator.exception.ScoringException;
import com.remizov.calculator.service.CreditService;
import com.remizov.calculator.service.OfferService;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalculatorController.class)
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OfferService offerService;

    @MockitoBean
    private CreditService creditService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("POST /calculator/offers — успешный запрос, возвращает 4 предложения")
    void createOffers_validRequest_returns200WithOffers() throws Exception {
        List<LoanOfferDto> mockOffers = List.of(
                buildLoanOffer(false, false, new BigDecimal("25")),
                buildLoanOffer(false, true,  new BigDecimal("24")),
                buildLoanOffer(true,  false, new BigDecimal("22")),
                buildLoanOffer(true,  true,  new BigDecimal("21"))
        );
        when(offerService.createOffers(any())).thenReturn(mockOffers);

        mockMvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoanStatementRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].isInsuranceEnabled").value(false))
                .andExpect(jsonPath("$[2].isInsuranceEnabled").value(true));

        verify(offerService, times(1)).createOffers(any());
    }

    @Test
    @DisplayName("POST /calculator/offers — amount < 15000, валидация не пройдена → 400")
    void createOffers_amountTooSmall_returns400() throws Exception {
        LoanStatementRequestDto request = validLoanStatementRequest();
        request.setAmount(new BigDecimal("5000"));

        mockMvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(offerService);
    }

    @Test
    @DisplayName("POST /calculator/offers — term < 3, валидация не пройдена → 400")
    void createOffers_termTooSmall_returns400() throws Exception {
        LoanStatementRequestDto request = validLoanStatementRequest();
        request.setTerm(1);

        mockMvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(offerService);
    }

    @Test
    @DisplayName("POST /calculator/offers — некорректный email → 400")
    void createOffers_invalidEmail_returns400() throws Exception {
        LoanStatementRequestDto request = validLoanStatementRequest();
        request.setEmail("not-an-email");

        mockMvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(offerService);
    }

    @Test
    @DisplayName("POST /calculator/offers — имя содержит цифры → 400")
    void createOffers_invalidFirstname_returns400() throws Exception {
        LoanStatementRequestDto request = validLoanStatementRequest();
        request.setFirstname("Ivan123");

        mockMvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(offerService);
    }

    @Test
    @DisplayName("POST /calculator/offers — клиент младше 18 лет → 400")
    void createOffers_underageBirthdate_returns400() throws Exception {
        LoanStatementRequestDto request = validLoanStatementRequest();
        request.setBirthdate(LocalDate.now().minusYears(10));

        mockMvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(offerService);
    }

    @Test
    @DisplayName("POST /calculator/offers — серия паспорта не 4 цифры → 400")
    void createOffers_invalidPassportSeries_returns400() throws Exception {
        LoanStatementRequestDto request = validLoanStatementRequest();
        request.setPassportSeries("12"); // должно быть 4 цифры

        mockMvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(offerService);
    }

    @Test
    @DisplayName("POST /calculator/offers — пустое тело запроса → 400")
    void createOffers_emptyBody_returns400() throws Exception {
        mockMvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(offerService);
    }

    @Test
    @DisplayName("POST /calculator/calc — успешный запрос, возвращает CreditDto")
    void createCredit_validRequest_returns200WithCredit() throws Exception {
        CreditDto mockCredit = buildCreditDto();
        when(creditService.createCredit(any())).thenReturn(mockCredit);

        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validScoringData())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100000))
                .andExpect(jsonPath("$.term").value(12))
                .andExpect(jsonPath("$.isInsuranceEnabled").value(false))
                .andExpect(jsonPath("$.isSalaryClient").value(true));

        verify(creditService, times(1)).createCredit(any());
    }

    @Test
    @DisplayName("POST /calculator/calc — ScoringException от сервиса → 422")
    void createCredit_scoringException_returns422() throws Exception {
        when(creditService.createCredit(any()))
                .thenThrow(new ScoringException("Отказ: безработный"));

        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validScoringData())))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.error").value("Отказ: безработный"));

        verify(creditService, times(1)).createCredit(any());
    }

    @Test
    @DisplayName("POST /calculator/calc — amount < 15000 → 400")
    void createCredit_amountTooSmall_returns400() throws Exception {
        ScoringDataDto request = validScoringData();
        request.setAmount(new BigDecimal("1000"));

        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(creditService);
    }

    @Test
    @DisplayName("POST /calculator/calc — term < 3 → 400")
    void createCredit_termTooSmall_returns400() throws Exception {
        ScoringDataDto request = validScoringData();
        request.setTerm(2);

        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(creditService);
    }

    @Test
    @DisplayName("POST /calculator/calc — клиент младше 18 лет → 400")
    void createCredit_underageBirthdate_returns400() throws Exception {
        ScoringDataDto request = validScoringData();
        request.setBirthdate(LocalDate.now().minusYears(16));

        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(creditService);
    }

    @Test
    @DisplayName("POST /calculator/calc — firstName содержит цифры → 400")
    void createCredit_invalidFirstName_returns400() throws Exception {
        ScoringDataDto request = validScoringData();
        request.setFirstName("Ivan777");

        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(creditService);
    }

    @Test
    @DisplayName("POST /calculator/calc — пустое тело запроса → 400")
    void createCredit_emptyBody_returns400() throws Exception {
        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(creditService);
    }

    private LoanStatementRequestDto validLoanStatementRequest() {
        LoanStatementRequestDto dto = new LoanStatementRequestDto();
        dto.setAmount(new BigDecimal("100000"));
        dto.setTerm(12);
        dto.setFirstname("Ivan");
        dto.setLastname("Petrov");
        dto.setEmail("ivan@example.com");
        dto.setBirthdate(LocalDate.of(1990, 1, 1));
        dto.setPassportSeries("1234");
        dto.setPassportNumber("123456");
        return dto;
    }

    private ScoringDataDto validScoringData() {
        ScoringDataDto dto = new ScoringDataDto();
        dto.setAmount(new BigDecimal("100000"));
        dto.setTerm(12);
        dto.setFirstName("Ivan");
        dto.setLastName("Petrov");
        dto.setBirthdate(LocalDate.of(1990, 1, 1));
        dto.setPassportSeries("1234");
        dto.setPassportNumber("123456");
        dto.setGender(ScoringDataDto.Gender.MALE);
        dto.setMaritalStatus(ScoringDataDto.MaritalStatus.MARRIED);
        dto.setIsInsuranceEnabled(false);
        dto.setIsSalaryClient(true);

        EmploymentDto employment = new EmploymentDto();
        employment.setEmploymentStatus(EmploymentDto.EmploymentStatus.EMPLOYED);
        employment.setSalary(new BigDecimal("50000"));
        employment.setPosition(EmploymentDto.Position.MID_MANAGER);
        employment.setWorkExperienceTotal(24);
        employment.setWorkExperienceCurrent(6);
        dto.setEmployment(employment);

        return dto;
    }

    private LoanOfferDto buildLoanOffer(boolean insurance, boolean salary, BigDecimal rate) {
        return LoanOfferDto.builder()
                .statementId(UUID.randomUUID())
                .requestedAmount(new BigDecimal("100000"))
                .totalAmount(new BigDecimal("105000"))
                .term(12)
                .monthlyPayment(new BigDecimal("9000"))
                .rate(rate)
                .isInsuranceEnabled(insurance)
                .isSalaryClient(salary)
                .build();
    }

    private CreditDto buildCreditDto() {
        return CreditDto.builder()
                .amount(new BigDecimal("100000"))
                .term(12)
                .monthlyPayment(new BigDecimal("9200"))
                .rate(new BigDecimal("22"))
                .psk(new BigDecimal("110400"))
                .isInsuranceEnabled(false)
                .isSalaryClient(true)
                .paymentSchedule(List.of())
                .build();
    }
}