package com.remizov.deal.entity;

import com.remizov.deal.enums.EmploymentPosition;
import com.remizov.deal.enums.EmploymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "employment")
public class Employment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "employment_uuid")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EmploymentStatus status;

    @Column(name = "employer_inn")
    private String employerInn;

    @Column(name = "salary")
    private BigDecimal salary;

    @Enumerated(EnumType.STRING)
    @Column(name = "position")
    private EmploymentPosition position;

    @Column(name = "work_experience_total")
    private Integer workExperienceTotal;

    @Column(name = "work_experience_current")
    private Integer workExperienceCurrent;
}