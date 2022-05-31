package com.migration.domain.persona.aggregation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.migration.domain.enums.TypeRegimeCompanion;
import com.migration.domain.persona.Persona;
import com.migration.domain.enums.MaritalStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "credi_persona_companion")
public class PersonaCompanion{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @JsonIgnore
    @OneToOne(mappedBy = "companion",
            cascade = CascadeType.ALL,
            targetEntity = Persona.class)
    private Persona persona;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id")
    private Persona data;

    @Enumerated(EnumType.STRING)
    @Column(name = "regime")
    private TypeRegimeCompanion type;
}
