package com.migration.domain.persona;

import com.migration.domain.Proposal;
import com.migration.domain.persona.aggregation.BalanceSheet;
import com.migration.domain.persona.aggregation.IncomeStatement;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
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

    @Column(name = "income")
    private BigDecimal income;

    @Column(name = "financial_commitment")
    private BigDecimal financialCommitment;

    @Column(name = "observation")
    private String observation;

    @Column(name = "serasa_risk_score")
    private Integer serasaRiskScore;

    @Column(name = "scr_score")
    private Integer scrScore;

    @Column(name = "due_diligence")
    private String dueDiligence;

    @Column(name = "total_income")
    private BigDecimal totalIncome;

    @Column(name = "total_financial_commitment")
    private BigDecimal totalFinancialCommitment;

    @Column(name = "total_commitment")
    private BigDecimal totalCommitment;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "credit_analysis_id", nullable = false)
    private List<BalanceSheet> balanceSheets = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "credit_analysis_id", nullable = false)
    private List<IncomeStatement> incomeStatements = new ArrayList<>();

}



