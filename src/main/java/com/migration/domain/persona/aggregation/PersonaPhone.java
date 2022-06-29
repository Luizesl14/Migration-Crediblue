package com.migration.domain.persona.aggregation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.migration.domain.persona.Persona;
import com.migration.domain.enums.CategoryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "credi_persona_phone")
public class PersonaPhone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "phone_id")
    private Phone phone;

    @JsonIgnore
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "persona_id", insertable = false, updatable = false)
    private Persona persona;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private CategoryType type;

    @Column(name = "principal")
    private Boolean principal;

    @Column(name = "created_at")
    private Date createdAt;

    @PrePersist
    public void persist() {
        this.createdAt = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonaPhone that = (PersonaPhone) o;
        return Objects.equals(phone, that.phone) && Objects.equals(persona, that.persona);
    }

}
