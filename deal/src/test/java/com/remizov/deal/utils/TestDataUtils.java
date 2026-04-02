package com.remizov.deal.utils;

import com.remizov.deal.dto.LoanOfferDto;
import com.remizov.deal.entity.Client;
import com.remizov.deal.entity.Passport;
import com.remizov.deal.entity.Statement;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

@UtilityClass
public class TestDataUtils {

    public static Statement createStatement(UUID id) {
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