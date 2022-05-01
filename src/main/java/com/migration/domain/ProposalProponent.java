package com.migration.domain;

import com.migration.domain.enums.ProponentType;
import com.migration.domain.persona.Persona;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ProposalProponent {

    private Integer id;
    private Proposal proposal;
    private Persona persona;
    private Double percentageOfCommitment;
    private ProponentType type;
    private Date createdAt = new Date();
}
