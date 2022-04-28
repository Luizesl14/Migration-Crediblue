package com.migration.domain.persona.aggregation;


import com.migration.domain.persona.Persona;
import com.migration.domain.enums.CompanyType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Company {
    private Integer id;
    private String corporateName;
    private String fantasyName;
    private Date foundationDate;
    private Date openingDate;
    private List<CompanyRepresentatives> representatives;
    private Persona persona;
    private String cnae;
    private CompanyType type;
}
