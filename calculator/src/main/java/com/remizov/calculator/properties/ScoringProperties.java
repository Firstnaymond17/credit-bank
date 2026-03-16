package com.remizov.calculator.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "scoring")
@Component
@Data
public class ScoringProperties {
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