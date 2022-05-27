package com.migration.infrastructure;

import com.migration.domain.Proposal;
import jakarta.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

@Resource
public interface IProposalRepository extends JpaRepository<Proposal, Integer> {
}
