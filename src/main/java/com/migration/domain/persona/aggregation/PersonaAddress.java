package com.migration.domain.persona.aggregation;

import com.migration.domain.persona.Persona;
import com.migration.domain.enums.AddressType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@Entity
public class PersonaAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "persona_id", insertable = false, updatable = false)
    private Persona persona;

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address data;
    private AddressType type;
    private Boolean principal;
    private Date createdAt;
}
