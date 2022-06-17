package com.migration.domain.persona;

import com.migration.domain.Proposal;
import com.migration.domain.persona.aggregation.BalanceSheet;
import com.migration.domain.persona.aggregation.IncomeStatement;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "credi_credit_analysis")
@Getter
@Setter
public class CreditAnalysis  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;

    @Column(name = "diligence")
    private String diligence;

    @Column(name = "observation")
    private String observation;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "credit_analysis_id", nullable = false)
    private List<BalanceSheet> balanceSheets = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "credit_analysis_id", nullable = false)
    private List<IncomeStatement> incomeStatements = new ArrayList<>();

}



