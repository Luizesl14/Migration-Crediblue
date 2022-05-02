package com.migration.domain.persona.aggregation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Entity
public class ComposeIncome {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String description;
    private BigDecimal monthlyRevenue;
    private BigDecimal totalRevenue;
    private BigDecimal monthlyExpenses;
    private BigDecimal amount;
}
