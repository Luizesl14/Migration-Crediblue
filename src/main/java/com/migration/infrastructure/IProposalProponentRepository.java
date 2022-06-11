package com.migration.infrastructure;

import com.migration.domain.ProposalProponent;
import com.migration.domain.enums.ProponentType;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProposalProponentRepository extends JpaRepository<ProposalProponent, Integer> {


    @Query("FROM ProposalProponent pp WHERE pp.persona.taxId = :taxId")
    List<ProposalProponent> findAllPersonaByTaxId(String taxId);

    @Query("FROM ProposalProponent pp WHERE pp.persona.id = :personaId AND pp.proposal.id = :proposalId ")
    ProposalProponent findAllDByProposalByPersona(Integer personaId, Integer proposalId);

    @Query("FROM ProposalProponent pp WHERE pp.persona.leadProposal.id =:leadId AND pp.proposal.id = :proposalId ")
    ProposalProponent findAllByProposalByLeadProposal(Integer leadId, Integer proposalId );

    @Query("SELECT pp FROM ProposalProponent pp WHERE pp.persona.cpfCnpj =:cpfCnpj " +
            "AND pp.proposal.id = :proposalId  AND pp.type = :proponentType")
    ProposalProponent virifyProponent(String cpfCnpj, Integer proposalId, ProponentType proponentType);



    @Query("FROM ProposalProponent pp JOIN FETCH  pp.persona.address  JOIN FETCH  pp.persona.proposal JOIN FETCH  pp.persona.leadProposal")
    List<ProposalProponent> findAllProponent();

}
