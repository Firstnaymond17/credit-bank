package com.remizov.deal.mapper;

import com.remizov.deal.dto.CreditDto;
import com.remizov.deal.entity.Credit;
import com.remizov.deal.enums.CreditStatus;
import java.util.ArrayList;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-02T01:09:35+0400",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class CreditMapperImpl extends CreditMapper {

    @Override
    public Credit toCredit(CreditDto creditDto) {
        if ( creditDto == null ) {
            return null;
        }

        Credit credit = new Credit();

        credit.setInsuranceEnabled( creditDto.getIsInsuranceEnabled() );
        credit.setSalaryClient( creditDto.getIsSalaryClient() );
        credit.setAmount( creditDto.getAmount() );
        credit.setTerm( creditDto.getTerm() );
        credit.setMonthlyPayment( creditDto.getMonthlyPayment() );
        credit.setRate( creditDto.getRate() );
        credit.setPsk( creditDto.getPsk() );

        credit.setPaymentSchedule( new ArrayList<>(creditDto.getPaymentSchedule()) );
        credit.setCreditStatus( CreditStatus.CALCULATED );

        return credit;
    }
}
