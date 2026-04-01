package com.remizov.deal.mapper;

import com.remizov.deal.dto.LoanStatementRequestDto;
import com.remizov.deal.entity.Client;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-02T01:47:46+0400",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class ClientMapperImpl extends ClientMapper {

    @Override
    public Client toClient(LoanStatementRequestDto request) {
        if ( request == null ) {
            return null;
        }

        Client client = new Client();

        client.setFirstName( request.getFirstname() );
        client.setLastName( request.getLastname() );
        client.setBirthDate( request.getBirthdate() );
        client.setPassport( createPassport( request ) );
        client.setMiddleName( request.getMiddleName() );
        client.setEmail( request.getEmail() );

        return client;
    }
}
