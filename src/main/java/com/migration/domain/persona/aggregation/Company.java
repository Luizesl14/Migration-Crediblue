package com.migration.domain.persona.aggregation;


import com.migration.domain.persona.Persona;
import com.migration.domain.enums.CompanyType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String corporateName;
    private String fantasyName;
    private Date foundationDate;
    private Date openingDate;

    @OneToMany
    @JoinColumn(name = "company_id")
    private List<CompanyRepresentatives> representatives;

    @OneToOne
    @JoinColumn(name = "persona_id", insertable = false, updatable = false)
    private Persona persona;

    private String cnae;
    private CompanyType type;
}
