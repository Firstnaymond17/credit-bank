package com.remizov.deal.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
public class Passport {

    private String series;

    private String number;

    private String issueBranch;

    private LocalDate issueDate;
}