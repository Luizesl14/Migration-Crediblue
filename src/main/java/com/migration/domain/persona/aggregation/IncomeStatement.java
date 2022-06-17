package com.migration.domain.persona.aggregation;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;

@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "credi_income_statement")
public class IncomeStatement  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "credit_analysis_comparative_id", insertable = false, updatable = false)
    private CreditAnalysisComparative creditAnalysisComparative;

    private String year;

    @Column(name = "gross_revenue")  //Receita Bruta
    private BigDecimal grossRevenue;

    @Column(name = "gross_profit")   //Lucro bruto
    private BigDecimal grossProfit;

    @Column(name = "profit_of_year") //Lucro do ano
    private BigDecimal profitOfYear;

    @Column(name = "profitability")
    private String profitability;   //Probabilidade
}

