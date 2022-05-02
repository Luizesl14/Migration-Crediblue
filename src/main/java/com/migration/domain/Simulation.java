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
public class Simulation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "lead_id")
    private Lead lead;

    @OneToOne
    @JoinColumn(name = "persona_id")
    private Persona persona;

    @OneToOne
    @JoinColumn(name = "partner_id")

    private Partner partner;
    private Integer installmentsAmount;

    @OneToOne
    @JoinColumn(name = "finder_id")
    private Finder finder;
    private String auditOriginClientIpAddress;

}
