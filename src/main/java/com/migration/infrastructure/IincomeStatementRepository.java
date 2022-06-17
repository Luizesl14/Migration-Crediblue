package com.migration.infrastructure;

import com.migration.domain.persona.aggregation.IncomeStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IincomeStatementRepository extends JpaRepository<IncomeStatement, Integer> {

}
