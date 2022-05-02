package com.migration.domain.persona.aggregation;

import com.migration.domain.persona.Persona;
import com.migration.domain.enums.CategoryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@Entity
public class PersonaPhone {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "phone_id")
    private Phone phone;

    @OneToOne
    @JoinColumn(name = "persona_id", insertable = false, updatable = false)
    private Persona persona;

    private CategoryType type;
    private Boolean principal;
    private Date createdAt;
}
