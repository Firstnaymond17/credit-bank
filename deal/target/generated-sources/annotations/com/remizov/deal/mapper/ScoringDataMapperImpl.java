package com.remizov.deal.mapper;

import com.remizov.deal.dto.FinishRegistrationRequestDto;
import com.remizov.deal.dto.LoanOfferDto;
import com.remizov.deal.dto.ScoringDataDto;
import com.remizov.deal.entity.Client;
import com.remizov.deal.entity.Passport;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-02T20:39:07+0400",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class ScoringDataMapperImpl extends ScoringDataMapper {

    @Override
    public ScoringDataDto toScoringData(Client client, LoanOfferDto appliedOffer, FinishRegistrationRequestDto request) {
        if ( client == null && appliedOffer == null && request == null ) {
            return null;
        }

        ScoringDataDto scoringDataDto = new ScoringDataDto();

        if ( client != null ) {
            scoringDataDto.setFirstName( client.getFirstName() );
            scoringDataDto.setLastName( client.getLastName() );
            scoringDataDto.setMiddleName( client.getMiddleName() );
            scoringDataDto.setBirthdate( client.getBirthDate() );
            scoringDataDto.setPassportSeries( clientPassportSeries( client ) );
            scoringDataDto.setPassportNumber( clientPassportNumber( client ) );
        }
        if ( appliedOffer != null ) {
            scoringDataDto.setAmount( appliedOffer.getRequestedAmount() );
            scoringDataDto.setTerm( appliedOffer.getTerm() );
            scoringDataDto.setIsInsuranceEnabled( appliedOffer.getIsInsuranceEnabled() );
            scoringDataDto.setIsSalaryClient( appliedOffer.getIsSalaryClient() );
        }
        if ( request != null ) {
            scoringDataDto.setGender( request.getGender() );
            scoringDataDto.setMaritalStatus( request.getMaritalStatus() );
            scoringDataDto.setDependentAmount( request.getDependentAmount() );
            scoringDataDto.setPassportIssueDate( request.getPassportIssueDate() );
            scoringDataDto.setPassportIssueBranch( request.getPassportIssueBranch() );
            scoringDataDto.setEmployment( request.getEmployment() );
            scoringDataDto.setAccountNumber( request.getAccountNumber() );
        }

        return scoringDataDto;
    }

    private String clientPassportSeries(Client client) {
        Passport passport = client.getPassport();
        if ( passport == null ) {
            return null;
        }
        return passport.getSeries();
    }

    private String clientPassportNumber(Client client) {
        Passport passport = client.getPassport();
        if ( passport == null ) {
            return null;
        }
        return passport.getNumber();
    }
}
