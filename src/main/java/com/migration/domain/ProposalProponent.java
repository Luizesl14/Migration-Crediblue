package com.migration.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.migration.domain.enums.ProponentType;
import com.migration.domain.persona.Persona;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

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

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
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
}