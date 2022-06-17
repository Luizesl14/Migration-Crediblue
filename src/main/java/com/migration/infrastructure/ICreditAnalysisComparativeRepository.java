package com.migration.infrastructure;

import com.migration.domain.persona.aggregation.CreditAnalysisComparative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICreditAnalysisComparativeRepository extends JpaRepository<CreditAnalysisComparative, Integer> {

}
