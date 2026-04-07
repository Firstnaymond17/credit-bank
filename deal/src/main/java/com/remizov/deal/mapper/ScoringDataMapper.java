package com.remizov.deal.mapper;

import com.remizov.deal.dto.FinishRegistrationRequestDto;
import com.remizov.deal.dto.LoanOfferDto;
import com.remizov.deal.dto.ScoringDataDto;
import com.remizov.deal.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ScoringDataMapper {

    @Mapping(target = "amount", source = "appliedOffer.requestedAmount")
    @Mapping(target = "term", source = "appliedOffer.term")
    @Mapping(target = "firstName", source = "client.firstName")
    @Mapping(target = "lastName", source = "client.lastName")
    @Mapping(target = "middleName", source = "client.middleName")
    @Mapping(target = "birthdate", source = "client.birthDate")
    @Mapping(target = "passportSeries", source = "client.passport.series")
    @Mapping(target = "passportNumber", source = "client.passport.number")
    @Mapping(target = "gender", source = "request.gender")
    @Mapping(target = "maritalStatus", source = "request.maritalStatus")
    @Mapping(target = "dependentAmount", source = "request.dependentAmount")
    @Mapping(target = "passportIssueDate", source = "request.passportIssueDate")
    @Mapping(target = "passportIssueBranch", source = "request.passportIssueBranch")
    @Mapping(target = "employment", source = "request.employment")
    @Mapping(target = "accountNumber", source = "request.accountNumber")
    @Mapping(target = "isInsuranceEnabled", source = "appliedOffer.isInsuranceEnabled")
    @Mapping(target = "isSalaryClient", source = "appliedOffer.isSalaryClient")
    public abstract ScoringDataDto toScoringData(Client client, LoanOfferDto appliedOffer, FinishRegistrationRequestDto request);
}