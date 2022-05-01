package com.migration.domain;

import com.migration.domain.persona.Persona;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Investor {

    private Integer id;
    private String cnpj;
    private String name;
    private String telephone;
    private String email;
    private Persona persona;
    private Boolean active = true;
}
