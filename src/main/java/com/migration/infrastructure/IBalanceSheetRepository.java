package com.migration.infrastructure;

import com.migration.domain.persona.aggregation.BalanceSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBalanceSheetRepository extends JpaRepository<BalanceSheet, Integer> {

}
