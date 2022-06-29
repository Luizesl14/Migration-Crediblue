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


    @Query("FROM ProposalProponent pp WHERE pp.persona.taxId is not null ")
    List<ProposalProponent> findAllPersonaByLeadPrincipal();

    @Query("FROM ProposalProponent pp WHERE pp.leadProposal is not null ORDER BY pp.leadProposal.id")
    List<ProposalProponent> findAllLeadPrincipal();

    @Query("FROM ProposalProponent pp WHERE pp.companion is not null ")
    List<ProposalProponent> findByCompanionId();

    @Query("FROM ProposalProponent pp WHERE pp.persona.leadProposal is null ")
    List<ProposalProponent> findAllPersonaBySecundario();

    @Query("FROM ProposalProponent pp WHERE pp.leadProposal is not null ")
    List<ProposalProponent> findAllPersonaByMain();

    @Query("FROM ProposalProponent pp WHERE pp.proposal is null and pp.leadProposal is not null ")
    List<ProposalProponent> findAllProposalNull();

    @Query("FROM ProposalProponent pp WHERE pp.type is null and pp.leadProposal is not null ")
    List<ProposalProponent> findAlltypeNull();

    @Query("FROM ProposalProponent pp WHERE pp.persona.id = :personaId AND pp.proposal.id = :proposalId ")
    ProposalProponent findAllDByProposalByPersona(Integer personaId, Integer proposalId);

    @Query("FROM ProposalProponent pp WHERE pp.persona.leadProposal.id =:leadId AND pp.proposal.id = :proposalId ")
    ProposalProponent findAllByProposalByLeadProposal(Integer leadId, Integer proposalId );

    @Query("FROM ProposalProponent pp WHERE pp.leadProposal.id =:leadId ")
    ProposalProponent findAllByProposalByLeadProposalMain(Integer leadId);

    @Query("SELECT pp FROM ProposalProponent pp WHERE pp.persona.cpfCnpj =:cpfCnpj " +
            "AND pp.proposal.id = :proposalId  AND pp.type = :proponentType")
    ProposalProponent virifyProponent(String cpfCnpj, Integer proposalId, ProponentType proponentType);



    @Query("FROM ProposalProponent pp JOIN FETCH  pp.persona.address  JOIN FETCH  pp.persona.proposal JOIN FETCH  pp.persona.leadProposal")
    List<ProposalProponent> findAllProponent();

}
