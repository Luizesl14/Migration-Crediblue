package com.migration.domain;

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
    private Boolean active = true;
}
