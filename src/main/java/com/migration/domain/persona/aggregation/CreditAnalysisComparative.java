package com.migration.domain.persona.aggregation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "credi_credit_analysis_comparative")
public class CreditAnalysisComparative  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "credit_analysis_comparative_id")
    private List<BalanceSheet> balanceSheets = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "credit_analysis_comparative_id")
    private List<IncomeStatement> incomeStatements = new ArrayList<>();

    @Column(name = "observation")
    private String observation;

}
