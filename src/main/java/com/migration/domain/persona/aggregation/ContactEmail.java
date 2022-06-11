package com.migration.domain.persona.aggregation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.migration.domain.persona.Persona;
import com.migration.domain.enums.EmailType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "credi_contact_email")
public class ContactEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "email")
    private String email;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "persona_id", updatable = false, insertable = false)
    private Persona persona;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private EmailType type;

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
        ContactEmail that = (ContactEmail) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}