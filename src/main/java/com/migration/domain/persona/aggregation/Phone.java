package com.migration.domain.persona.aggregation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Phone {

    private Integer id;
    private String number;
    private Boolean isWhatsApp;
}
