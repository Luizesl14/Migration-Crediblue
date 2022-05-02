package com.migration.domain.persona.aggregation;

import com.migration.domain.persona.Persona;
import com.migration.domain.enums.RepresentativeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CompanyRepresentatives {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "persona_id")
    private Persona representative;

    @OneToOne
    @JoinColumn(name = "company_id")
    private Company company;
    private RepresentativeType type;
}
