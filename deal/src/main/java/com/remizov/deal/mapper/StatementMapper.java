package com.remizov.deal.mapper;

import com.remizov.deal.entity.Client;
import com.remizov.deal.entity.Statement;
import com.remizov.deal.enums.ApplicationStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class, ArrayList.class, ApplicationStatus.class})
public abstract class StatementMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "credit", ignore = true)
    @Mapping(target = "appliedOffer", ignore = true)
    @Mapping(target = "signDate", ignore = true)
    @Mapping(target = "sesCode", ignore = true)
    @Mapping(target = "status", expression = "java(ApplicationStatus.PREAPPROVAL)")
    @Mapping(target = "creationDate", expression = "java(LocalDateTime.now())")
    @Mapping(target = "statusHistory", expression = "java(new ArrayList<>())")
    public abstract Statement toStatement(Client client);
}