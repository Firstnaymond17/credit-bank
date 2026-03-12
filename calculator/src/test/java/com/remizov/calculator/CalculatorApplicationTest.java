package com.remizov.calculator;

import com.remizov.calculator.dto.*;
import com.remizov.calculator.exception.ScoringException;
import com.remizov.calculator.service.impl.CalculatorServiceImpl;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CalculatorApplicationTest {

    @InjectMocks
    private CalculatorServiceImpl calculatorService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(calculatorService, "baseRate", BigDecimal.valueOf(25));
    }

    private ScoringDataDto scoringRequest() {
        EmploymentDto empDto = new EmploymentDto();
        empDto.setEmploymentStatus(EmploymentDto.EmploymentStatus.EMPLOYED);
        empDto.setPosition(EmploymentDto.Position.MID_MANAGER);
        empDto.setSalary(BigDecimal.valueOf(100_000));
        empDto.setWorkExperienceTotal(24);
        empDto.setWorkExperienceCurrent(6);

        ScoringDataDto scoringDto = new ScoringDataDto();
        scoringDto.setAmount(BigDecimal.valueOf(100_000));
        scoringDto.setTerm(12);
        scoringDto.setFirstName("Ivan");
        scoringDto.setLastName("Ivanov");
        scoringDto.setMiddleName("Ivanovich");
        scoringDto.setGender(ScoringDataDto.Gender.MALE);
        scoringDto.setBirthdate(LocalDate.of(1990, 1, 1));
        scoringDto.setMaritalStatus(ScoringDataDto.MaritalStatus.MARRIED);
        scoringDto.setIsInsuranceEnabled(false);
        scoringDto.setIsSalaryClient(false);
        scoringDto.setPassportSeries("6325");
        scoringDto.setPassportNumber("123456");
        scoringDto.setEmployment(empDto);
        return scoringDto;
    }

    @Test
    @DisplayName("createOffers — должен вернуть 4 оффера")
    void createOffers_returnsFourOffers() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(BigDecimal.valueOf(100_000));
        request.setTerm(12);

        List<LoanOfferDto> offers = calculatorService.createOffers(request);

        assertThat(offers).hasSize(4);
    }

    @Test
    @DisplayName("createOffers — ставки для всех 4 комбинаций верные")
    void createOffers_ratesAreCorrect() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(BigDecimal.valueOf(100_000));
        request.setTerm(12);

        List<LoanOfferDto> offers = calculatorService.createOffers(request);

        assertThat(offers).anySatisfy(o -> {
            assertThat(o.getIsInsuranceEnabled()).isFalse();
            assertThat(o.getIsSalaryClient()).isFalse();
            assertThat(o.getRate()).isEqualByComparingTo(BigDecimal.valueOf(25));
        });

        assertThat(offers).anySatisfy(o -> {
            assertThat(o.getIsInsuranceEnabled()).isTrue();
            assertThat(o.getIsSalaryClient()).isFalse();
            assertThat(o.getRate()).isEqualByComparingTo(BigDecimal.valueOf(22));
            assertThat(o.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(105_000));
        });

        assertThat(offers).anySatisfy(o -> {
            assertThat(o.getIsInsuranceEnabled()).isFalse();
            assertThat(o.getIsSalaryClient()).isTrue();
            assertThat(o.getRate()).isEqualByComparingTo(BigDecimal.valueOf(24));
        });

        assertThat(offers).anySatisfy(o -> {
            assertThat(o.getIsInsuranceEnabled()).isTrue();
            assertThat(o.getIsSalaryClient()).isTrue();
            assertThat(o.getRate()).isEqualByComparingTo(BigDecimal.valueOf(21));
        });
    }

    @Test
    @DisplayName("createCredit — ставка = 17 (25 - 2 MID_MANAGER - 3 MARRIED - 3 MALE)")
    void createCredit_rateCalculatedCorrectly() {
        ScoringDataDto request = scoringRequest();

        CreditDto credit = calculatorService.createCredit(request);

        assertThat(credit.getRate()).isEqualByComparingTo(BigDecimal.valueOf(17));
    }

    @Test
    @DisplayName("createCredit — график платежей содержит 12 элементов")
    void createCredit_scheduleHasCorrectSize() {
        ScoringDataDto request = scoringRequest();

        CreditDto credit = calculatorService.createCredit(request);

        assertThat(credit.getPaymentSchedule()).hasSize(12);
    }

    @Test
    @DisplayName("createCredit — UNEMPLOYED должен получить ScoringException")
    void createCredit_unemployed_throwsException() {
        ScoringDataDto request = scoringRequest();
        request.getEmployment().setEmploymentStatus(EmploymentDto.EmploymentStatus.UNEMPLOYED);

        assertThatThrownBy(() -> calculatorService.createCredit(request))
                .isInstanceOf(ScoringException.class)
                .hasMessageContaining("безработный");
    }

    @Test
    @DisplayName("createCredit — сумма > 24 зарплат должна дать ScoringException")
    void createCredit_amountExceedsSalary_throwsException() {
        ScoringDataDto request = scoringRequest();
        request.setAmount(BigDecimal.valueOf(3_000_000));
        request.getEmployment().setSalary(BigDecimal.valueOf(100_000));

        assertThatThrownBy(() -> calculatorService.createCredit(request))
                .isInstanceOf(ScoringException.class)
                .hasMessageContaining("24 зарплаты");
    }

    @Test
    @DisplayName("createCredit — возраст 19 лет должен дать ScoringException")
    void createCredit_ageLessThan20_throwsException() {
        ScoringDataDto request = scoringRequest();
        request.setBirthdate(LocalDate.now().minusYears(19));

        assertThatThrownBy(() -> calculatorService.createCredit(request))
                .isInstanceOf(ScoringException.class)
                .hasMessageContaining("возраст");
    }

    @Test
    @DisplayName("createCredit — возраст 66 лет должен дать ScoringException")
    void createCredit_ageMoreThan65_throwsException() {
        ScoringDataDto request = scoringRequest();
        request.setBirthdate(LocalDate.now().minusYears(66));

        assertThatThrownBy(() -> calculatorService.createCredit(request))
                .isInstanceOf(ScoringException.class)
                .hasMessageContaining("возраст");
    }

    @Test
    @DisplayName("createCredit — общий стаж 17 мес должен дать ScoringException")
    void createCredit_totalExperienceLessThan18_throwsException() {
        ScoringDataDto request = scoringRequest();
        request.getEmployment().setWorkExperienceTotal(17);

        assertThatThrownBy(() -> calculatorService.createCredit(request))
                .isInstanceOf(ScoringException.class)
                .hasMessageContaining("общий стаж");
    }

    @Test
    @DisplayName("createCredit — текущий стаж 2 мес должен дать ScoringException")
    void createCredit_currentExperienceLessThan3_throwsException() {
        ScoringDataDto request = scoringRequest();
        request.getEmployment().setWorkExperienceCurrent(2);

        assertThatThrownBy(() -> calculatorService.createCredit(request))
                .isInstanceOf(ScoringException.class)
                .hasMessageContaining("текущий стаж");
    }
}