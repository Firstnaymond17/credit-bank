package com.remizov.calculator.service;

import com.remizov.calculator.dto.*;
import com.remizov.calculator.exception.ScoringException;
import com.remizov.calculator.properties.ScoringProperties;
import com.remizov.calculator.service.impl.CreditServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class CreditServiceTest {

    @Mock
    private ScoringProperties scoringProperties;

    @InjectMocks
    private CreditServiceImpl creditService;

    @BeforeEach
    void setUp() {
        lenient().when(scoringProperties.getBaseRate()).thenReturn(25.0);
        lenient().when(scoringProperties.getInsuranceRateDiscount()).thenReturn(3);
        lenient().when(scoringProperties.getSalaryClientRateDiscount()).thenReturn(1);
        lenient().when(scoringProperties.getSelfEmployedRateIncrease()).thenReturn(2);
        lenient().when(scoringProperties.getBusinessOwnerRateIncrease()).thenReturn(1);
        lenient().when(scoringProperties.getMidManagerRateDiscount()).thenReturn(2);
        lenient().when(scoringProperties.getTopManagerRateDiscount()).thenReturn(3);
        lenient().when(scoringProperties.getMaxSalaryMultiplier()).thenReturn(24);
        lenient().when(scoringProperties.getMarriedRateDiscount()).thenReturn(3);
        lenient().when(scoringProperties.getDivorcedRateIncrease()).thenReturn(1);
        lenient().when(scoringProperties.getMinPossibleAge()).thenReturn(20);
        lenient().when(scoringProperties.getMaxPossibleAge()).thenReturn(65);
        lenient().when(scoringProperties.getFemaleMinAge()).thenReturn(32);
        lenient().when(scoringProperties.getFemaleMaxAge()).thenReturn(60);
        lenient().when(scoringProperties.getMaleMinAge()).thenReturn(30);
        lenient().when(scoringProperties.getMaleMaxAge()).thenReturn(55);
        lenient().when(scoringProperties.getMinWorkExperienceTotal()).thenReturn(18);
        lenient().when(scoringProperties.getMinWorkExperienceCurrent()).thenReturn(3);
    }

    @Test
    @DisplayName("createCredit — ставка = 20 (25 - 2 MID_MANAGER - 3 MARRIED)")
    void createCredit_rateCalculatedCorrectly() {
        CreditDto credit = creditService.createCredit(scoringRequest());

        assertEquals(0, credit.getRate().compareTo(BigDecimal.valueOf(20)));
    }

    @Test
    @DisplayName("createCredit — график платежей содержит 12 элементов")
    void createCredit_scheduleHasCorrectSize() {
        CreditDto credit = creditService.createCredit(scoringRequest());

        assertEquals(12, credit.getPaymentSchedule().size());
    }

    @ParameterizedTest(name = "[{index}] {1}")
    @MethodSource("scoringExceptionCases")
    @DisplayName("createCredit — ScoringException при разных условиях скоринга")
    void createCredit_scoringException_parameterized(ScoringDataDto request, String expectedMessage) {
        ScoringException exception = assertThrows(ScoringException.class,
                () -> creditService.createCredit(request));

        assertEquals(expectedMessage, exception.getMessage());
    }

    static Stream<Arguments> scoringExceptionCases() {
        return Stream.of(
                Arguments.of(withUnemployed(), "Отказ: безработный"),
                Arguments.of(withAmountExceedsSalary(), "Отказ: сумма займа превышает 24 зарплаты"),
                Arguments.of(withAgeTooYoung(), "Отказ: возраст вне диапазона"),
                Arguments.of(withAgeTooOld(), "Отказ: возраст вне диапазона"),
                Arguments.of(withLowTotalExperience(), "Отказ: общий стаж меньше 18 месяцев"),
                Arguments.of(withLowCurrentExperience(), "Отказ: текущий стаж меньше 3 месяцев")
        );
    }

    private static ScoringDataDto withUnemployed() {
        ScoringDataDto dto = scoringRequest();
        dto.getEmployment().setEmploymentStatus(EmploymentDto.EmploymentStatus.UNEMPLOYED);
        return dto;
    }

    private static ScoringDataDto withAmountExceedsSalary() {
        ScoringDataDto dto = scoringRequest();
        dto.setAmount(BigDecimal.valueOf(3_000_000));
        return dto;
    }

    private static ScoringDataDto withAgeTooYoung() {
        ScoringDataDto dto = scoringRequest();
        dto.setBirthdate(LocalDate.now().minusYears(19));
        return dto;
    }

    private static ScoringDataDto withAgeTooOld() {
        ScoringDataDto dto = scoringRequest();
        dto.setBirthdate(LocalDate.now().minusYears(66));
        return dto;
    }

    private static ScoringDataDto withLowTotalExperience() {
        ScoringDataDto dto = scoringRequest();
        dto.getEmployment().setWorkExperienceTotal(17);
        return dto;
    }

    private static ScoringDataDto withLowCurrentExperience() {
        ScoringDataDto dto = scoringRequest();
        dto.getEmployment().setWorkExperienceCurrent(2);
        return dto;
    }

    private static ScoringDataDto scoringRequest() {
        EmploymentDto empDto = new EmploymentDto();
        empDto.setEmploymentStatus(EmploymentDto.EmploymentStatus.EMPLOYED);
        empDto.setPosition(EmploymentDto.Position.MID_MANAGER);
        empDto.setSalary(BigDecimal.valueOf(100_000));
        empDto.setWorkExperienceTotal(24);
        empDto.setWorkExperienceCurrent(6);

        ScoringDataDto dto = new ScoringDataDto();
        dto.setAmount(BigDecimal.valueOf(100_000));
        dto.setTerm(12);
        dto.setFirstName("Ivan");
        dto.setLastName("Ivanov");
        dto.setMiddleName("Ivanovich");
        dto.setGender(ScoringDataDto.Gender.MALE);
        dto.setBirthdate(LocalDate.of(1990, 1, 1));
        dto.setMaritalStatus(ScoringDataDto.MaritalStatus.MARRIED);
        dto.setIsInsuranceEnabled(false);
        dto.setIsSalaryClient(false);
        dto.setPassportSeries("6325");
        dto.setPassportNumber("123456");
        dto.setEmployment(empDto);
        return dto;
    }

}