package com.migration.infrastructure;

import com.migration.domain.Finder;
import com.migration.domain.LeadProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILeadProposalRespository extends JpaRepository<LeadProposal, Integer> {
}
