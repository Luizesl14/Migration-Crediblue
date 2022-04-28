package com.migration.domain.persona.aggregation;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ComposeIncome {

    private Integer id;
    private String description;
    private BigDecimal amount;
}
