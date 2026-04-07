package com.remizov.deal.mapper;

import com.remizov.deal.dto.LoanStatementRequestDto;
import com.remizov.deal.entity.Client;
import com.remizov.deal.entity.Passport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public abstract class ClientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "firstName", source = "firstname")
    @Mapping(target = "lastName", source = "lastname")
    @Mapping(target = "birthDate", source = "birthdate")
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "maritalStatus", ignore = true)
    @Mapping(target = "dependentAmount", ignore = true)
    @Mapping(target = "employment", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "passport", source = "request", qualifiedByName = "createPassport")
    public abstract Client toClient(LoanStatementRequestDto request);

    @Named("createPassport")
    protected Passport createPassport(LoanStatementRequestDto request) {
        Passport passport = new Passport();
        passport.setSeries(request.getPassportSeries());
        passport.setNumber(request.getPassportNumber());
        return passport;
    }
}