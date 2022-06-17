package com.migration.domain.persona.aggregation;

import com.migration.domain.ProposalProponent;
import com.migration.domain.persona.CreditAnalysis;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "credi_credit_analysis_document")
public class CreditAnalysisDocuement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "credit_analysis_id")
    private CreditAnalysis creditAnalysisId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "document_id")
    private Document document;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "main_proponent_id")
    private ProposalProponent proponent;



}
