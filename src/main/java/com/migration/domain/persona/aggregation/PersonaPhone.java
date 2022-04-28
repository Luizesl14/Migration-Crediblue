package com.migration.domain.persona.aggregation;

import com.migration.domain.persona.Persona;
import com.migration.domain.enums.CategoryType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PersonaPhone {

    private Integer id;
    private Phone phone;
    private Persona persona;
    private CategoryType type;
    private Boolean principal;
    private Date createdAt;
}
