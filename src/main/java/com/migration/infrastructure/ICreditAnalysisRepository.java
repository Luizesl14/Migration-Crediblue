package com.migration.infrastructure;

import com.migration.domain.persona.CreditAnalysis;
import com.migration.domain.persona.aggregation.CreditAnalysisDocuement;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICreditAnalysisRepository extends JpaRepository<CreditAnalysis, Integer> {

    CreditAnalysis findCreditAnalysisByProposalId(Integer id);

}
