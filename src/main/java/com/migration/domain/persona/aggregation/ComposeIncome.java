package com.migration.domain.persona.aggregation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;

@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@Entity
@Table(name = "credi_compose_income")
public class ComposeIncome{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private BigDecimal amount;
}