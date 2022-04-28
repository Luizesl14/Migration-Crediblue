package com.migration.domain.persona.aggregation;

import com.migration.domain.persona.Persona;
import com.migration.domain.enums.RepresentativeType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyRepresentatives {

    private Integer id;
    private Persona representative;
    private Company company;
    private RepresentativeType type;
}
