package com.migration.infrastructure;

import com.migration.domain.ProposalProponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProposalProponentRepository extends JpaRepository<ProposalProponent, Integer> {


    @Query("FROM ProposalProponent pp WHERE pp.persona.taxId = :taxId")
    List<ProposalProponent> findAllPersonaByTaxId(String taxId);

}
