package com.migration.infrastructure;

import com.migration.domain.persona.aggregation.CreditAnalysisProponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICreditAnalysisProponentRepository extends JpaRepository<CreditAnalysisProponent, Integer> {

}
