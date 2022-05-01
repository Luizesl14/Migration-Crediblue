package com.migration.infrastructure;

import com.migration.domain.Proposal;
import com.migration.domain.Simulation;
import jakarta.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

@Resource
public interface ProposalRepository extends JpaRepository<Proposal, Integer> {
}
