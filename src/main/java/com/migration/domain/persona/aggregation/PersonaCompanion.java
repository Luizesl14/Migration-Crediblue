package com.migration.domain.persona.aggregation;

import com.migration.domain.enums.TypeRegimeCompanion;
import com.migration.domain.persona.Persona;
import com.migration.domain.enums.MaritalStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonaCompanion {

    private Integer id;
    private Persona persona;
    private Persona data;
    private MaritalStatus type;
    private TypeRegimeCompanion regime;
}
