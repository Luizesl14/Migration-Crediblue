package com.migration.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.migration.domain.enums.MaritalStatus;
import com.migration.domain.enums.ProponentType;
import com.migration.domain.enums.TypeRegimeCompanion;
import com.migration.domain.persona.Companion;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.PersonaMigration;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "credi_proposal_proponent")
public class ProposalProponent{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    private Proposal proposal;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "persona_id")
    private Persona persona;

    @Column(name = "percentage_of_commitment")
    private Double percentageOfCommitment;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ProponentType type;

    @Column(name = "compose_income")
    private Boolean composeIncome;

    @Column(name = "monthly_income")
    private BigDecimal monthlyIncome;

    @Column(name = "scr_consulted")
    private Boolean scrConsulted;

    @Column(name = "created_at")
    private Date createdAt = new Date();


    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status")
    private MaritalStatus maritalStatus;

    @OneToOne
    @JoinColumn(name = "companion_id")
    private Companion companion;

    @Enumerated(EnumType.STRING)
    @Column(name = "regime")
    private TypeRegimeCompanion typeRegimeCompanion;

    @OneToOne
    @JoinColumn(name = "persona_migration_id")
    private PersonaMigration personaMigration;

    @OneToOne
    @JoinColumn(name = "lead_proposal")
    private LeadProposal leadProposal;

    @OneToOne
    @JoinColumn(name = "old_persona")
    private Persona oldPersona;

    @Column(name = "spouse_name")
    private String spouseName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProposalProponent proponent = (ProposalProponent) o;
        return Objects.equals(proposal, proponent.proposal) && Objects.equals(persona, proponent.persona) && type == proponent.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(proposal, persona, type);
    }
}