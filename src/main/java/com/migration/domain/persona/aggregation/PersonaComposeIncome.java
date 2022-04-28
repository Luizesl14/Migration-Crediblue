package com.migration.domain.persona.aggregation;

import com.migration.domain.persona.Persona;
import com.migration.domain.enums.IncomeType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonaComposeIncome {

    private Integer id;
    private ComposeIncome composeIncome;
    private IncomeType type;
    private Persona persona;
}
