package com.migration.domain;

import com.migration.domain.persona.Persona;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "credi_investor")
public class Investor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String cnpj;
    private String name;
    private String telephone;
    private String email;

    @OneToOne
    @JoinColumn(name = "persona_id")
    private Persona persona;
    private Boolean active = true;
}
