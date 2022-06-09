package com.migration.infrastructure;

import com.migration.domain.Proposal;
import jakarta.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Resource
public interface IProposalRepository extends JpaRepository<Proposal, Integer> {

    @Query("FROM Proposal  p WHERE  p.id =:id ")
    Proposal findByproposalId(Integer id);

    @Query("SELECT p FROM Proposal p JOIN FETCH p.leadProposal")
    List<Proposal> findAllByProposal();

}
