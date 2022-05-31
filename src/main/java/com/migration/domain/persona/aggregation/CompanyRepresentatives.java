package com.migration.domain.persona.aggregation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.migration.domain.persona.Persona;
import com.migration.domain.enums.RepresentativeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "credi_company_representative")
public class CompanyRepresentatives  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "persona_id")
    private Persona representative;

    @JsonIgnore
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToOne
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private RepresentativeType type;

    @Column(name = "percent_participation")
    private Double percentParticipation;
}
