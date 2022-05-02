package com.migration.domain;

import com.migration.domain.enums.ProponentType;
import com.migration.domain.persona.Persona;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@Entity
public class ProposalProponent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;

    @OneToOne
    @JoinColumn(name = "persona_id")
    private Persona persona;
    private Double percentageOfCommitment;
    private ProponentType type;
    private Date createdAt = new Date();
}
