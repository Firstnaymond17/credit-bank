package com.remizov.calculator.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "scoring")
@Getter
@Setter
public class ScoringProperties {

    private double baseRate = 25;
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