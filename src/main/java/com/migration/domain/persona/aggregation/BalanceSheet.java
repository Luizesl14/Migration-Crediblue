package com.migration.domain.persona.aggregation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.migration.domain.enums.EmailType;
import com.migration.domain.persona.Persona;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;


@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "credi_balance_sheet")
public class BalanceSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "credit_analysis_comparative_id", insertable = false, updatable = false)
    private CreditAnalysisComparative creditAnalysisComparative;

    @Column(name = "name")
    private String name;

    @Column(name = "year")
    private String year;

    @Column(name = "assets")
    private BigDecimal assets;

    @Column(name = "current_assets")
    private BigDecimal currentAssets;

    @Column(name = "current_assets_financial_commitment")
    private BigDecimal currentAssetsFinancialCommitment;

    @Column(name = "non_current_assets")
    private BigDecimal nonCurrentAssets;

    @Column(name = "non_current_assets_financial_commitment")
    private BigDecimal nonCurrentAssetsFinancialCommitment;

    private BigDecimal liabilities;

    @Column(name = "current_liabilities")
    private BigDecimal currentLiabilities;

    @Column(name = "current_liabilities_financial_commitment")
    private BigDecimal currentLiabilitiesFinancialCommitment;

    @Column(name = "non_current_liabilities")
    private BigDecimal nonCurrentLiabilities;

    @Column(name = "non_current_liabilities_financial_commitment")
    private BigDecimal nonCurrentLiabilitiesFinancialCommitment;
}