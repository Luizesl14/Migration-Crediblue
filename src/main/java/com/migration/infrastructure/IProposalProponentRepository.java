package com.migration.infrastructure;

import com.migration.domain.ProposalProponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProposalProponentRepository extends JpaRepository<ProposalProponent, Integer> {

}
