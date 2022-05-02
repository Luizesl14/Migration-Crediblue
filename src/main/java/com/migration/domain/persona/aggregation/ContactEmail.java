package com.migration.domain.persona.aggregation;

import com.migration.domain.persona.Persona;
import com.migration.domain.enums.EmailType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@Entity
public class ContactEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String email;

    @OneToOne
    @JoinColumn(name = "persona_id", insertable = false, updatable = false)
    private Persona persona;

    private EmailType type;
    private Boolean principal;
    private Date createdAt;
}
