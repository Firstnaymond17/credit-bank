package com.remizov.deal.mapper;

import com.remizov.deal.dto.CreditDto;
import com.remizov.deal.entity.Credit;
import com.remizov.deal.enums.CreditStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;

@Mapper(componentModel = "spring", imports = {ArrayList.class, CreditStatus.class})
public abstract class CreditMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "insuranceEnabled", source = "isInsuranceEnabled")
    @Mapping(target = "salaryClient", source = "isSalaryClient")
    @Mapping(target = "paymentSchedule", expression = "java(new ArrayList<>(creditDto.getPaymentSchedule()))")
    @Mapping(target = "creditStatus", expression = "java(CreditStatus.CALCULATED)")
    public abstract Credit toCredit(CreditDto creditDto);
}