package com.remizov.deal.mapper;

import com.remizov.deal.entity.Client;
import com.remizov.deal.entity.Statement;
import com.remizov.deal.entity.enums.ApplicationStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-03T17:33:28+0400",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class StatementMapperImpl extends StatementMapper {

    @Override
    public Statement toStatement(Client client) {
        if ( client == null ) {
            return null;
        }

        Statement statement = new Statement();

        statement.setClient( client );

        statement.setStatus( ApplicationStatus.PREAPPROVAL );
        statement.setCreationDate( LocalDateTime.now() );
        statement.setStatusHistory( new ArrayList<>() );

        return statement;
    }
}
