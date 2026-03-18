package com.remizov.calculator.properties;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "scoring")
@Getter
public class ScoringProperties {

    private final BigDecimal baseRate = BigDecimal.valueOf(25);

    private int minPossibleAge;
    private int maxPossibleAge;
    private int insuranceRateDiscount;
    private int salaryClientRateDiscount;
    private int selfEmployedRateIncrease;
    private int businessOwnerRateIncrease;
    private int midManagerRateDiscount;
    private int topManagerRateDiscount;
    private int marriedRateDiscount;
    private int divorcedRateIncrease;
    private int maxSalaryMultiplier;
    private int femaleMinAge;
    private int femaleMaxAge;
    private int maleMinAge;
    private int maleMaxAge;
    private int minWorkExperienceTotal;
    private int minWorkExperienceCurrent;
}