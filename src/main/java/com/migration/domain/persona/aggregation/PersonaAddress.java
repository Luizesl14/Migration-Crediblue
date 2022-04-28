package com.migration.domain.persona.aggregation;

import com.migration.domain.persona.Persona;
import com.migration.domain.enums.AddressType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PersonaAddress {

    private Integer id;
    private Persona persona;
    private Address data;
    private AddressType type;
    private Boolean principal;
    private Date createdAt;
}
