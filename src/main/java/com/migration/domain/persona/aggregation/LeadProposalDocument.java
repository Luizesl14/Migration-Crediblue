package com.migration.domain.persona.aggregation;

import com.migration.domain.LeadProposal;
import com.migration.domain.ProposalProponent;
import com.migration.domain.persona.Persona;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "credi_lead_proposal_document")
public class LeadProposalDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lead_proposal_id")
    private LeadProposal leadProposal;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "main_proponent_id")
    private ProposalProponent proponent;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "document_id")
    private Document document;

}
