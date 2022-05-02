package com.migration.domain.persona.aggregation;

import com.migration.domain.enums.TypeRegimeCompanion;
import com.migration.domain.persona.Persona;
import com.migration.domain.enums.MaritalStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class PersonaCompanion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "persona_id", insertable = false, updatable = false)
    private Persona persona;

    @OneToOne
    @JoinColumn(name = "persona_id")
    private Persona data;

    private MaritalStatus type;
    private TypeRegimeCompanion regime;
}
