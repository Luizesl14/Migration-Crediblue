package com.migration.domain.persona.aggregation;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.migration.domain.ProposalProponent;
import com.migration.domain.persona.CreditAnalysis;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "credi_credit_analysis_proponent")
public class CreditAnalysisProponent  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    private CreditAnalysis creditAnalysis;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "analysis_comparative_id")
    private CreditAnalysisComparative analysisComparative;

    @OneToOne(cascade = CascadeType.ALL)
    private ProposalProponent proponent;

    @Column(name = "total_commitment")   // Compromisso Total
    private BigDecimal totalCommitment;

    @Column(name = "total_financial_commitment") //Compromisso financeiro total
    private BigDecimal totalFinancialCommitment;

    @Column(name = "financial_commitment")   //Compromisso financeiro
    private BigDecimal financialCommitment;

    @Column(name = "mma_commitment_financial_scr")
    private BigDecimal mmaCommitmentFinancialScr;

    @Column(name = "scr_analysis")
    private String scrAnalysis;

    @Column(name = "serasa_risk_score")
    private Integer serasaRiskScore;

    @Column(name = "scr_score")
    private Integer scrScore;

    @Column(name = "due_diligence") //Diligencia Devida
    private String dueDiligence;

    @Column(name = "total_income")   //Renda Total
    private BigDecimal totalIncome;

    @Column(name = "income")
    private BigDecimal income;      //renda

    @Column(name = "observation_subjetive_analysis")
    private String observationSubjetiveAnalysis;

    @Column(name = "created_at")
    private Date createdAt;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, targetEntity = Document.class)
    @JoinTable(name = "credi_persona_document",
            joinColumns = {@JoinColumn(name = "persona_id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "document_id", nullable = false)})
    private List<Document> documents;

}