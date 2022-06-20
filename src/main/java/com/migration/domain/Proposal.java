package com.migration.domain;


import com.migration.domain.persona.CreditAnalysis;
import com.migration.domain.persona.Persona;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "credi_proposal")
public class Proposal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "simulation_id")
    private Simulation simulation;


    @OneToOne
    @JoinColumn(name = "partner_id")
    private Partner partner;

    @OneToMany
    @JoinColumn(name = "investor_id")
    private List<Investor> investors;

    @OneToMany
    @JoinColumn(name = "user_id")
    private List<User> users;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lead_proposal_id")
    private LeadProposal leadProposal;

    @OneToMany
    @JoinColumn(name = "proposal_id")
    private List<ProposalProponent> proponents;

    @OneToMany
    private List<Persona> personas;

    private Boolean sameAddressWarrantyHome;
    private Integer installmentsAmount;


}
