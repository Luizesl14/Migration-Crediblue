package com.migration.domain.persona.aggregation;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.migration.domain.enums.CompanyType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;
import java.util.List;


@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@Entity
@Table(name = "credi_company")
public class Company  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "corporate_name")
    private String corporateName;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "fantasy_name")
    private String fantasyName;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "foundation_date")
    private Date foundationDate;

    @Column(name = "opening_date")
    private Date openingDate;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_id")
    private List<CompanyRepresentatives> representatives;

    @Column(name = "cnae")
    private String cnae;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private CompanyType type;

}