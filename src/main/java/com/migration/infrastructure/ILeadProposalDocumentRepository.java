package com.migration.infrastructure;

import com.migration.domain.persona.aggregation.LeadProposalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILeadProposalDocumentRepository extends JpaRepository<LeadProposalDocument, Integer> {
}
