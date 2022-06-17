package com.migration.infrastructure;

import com.migration.domain.LeadProposal;
import com.migration.domain.ProposalProponent;
import com.migration.domain.persona.aggregation.CreditAnalysisDocuement;
import com.migration.domain.persona.aggregation.LeadProposalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ICreditAnalysisDocumentRepository extends JpaRepository<CreditAnalysisDocuement, Integer> {
    ;
}
