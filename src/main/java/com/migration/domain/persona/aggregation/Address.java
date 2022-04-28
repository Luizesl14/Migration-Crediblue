package com.migration.domain.persona.aggregation;

import com.migration.domain.enums.UF;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Address {

    private Integer id;
    private String street;
    private String neighborhood;
    private String number;
    private String complement;
    private String cep;
    private String city;
    private UF uf;
    private String country;
    private String ibge;
    private Location location;
    private LocalDateTime createdAt;
}
