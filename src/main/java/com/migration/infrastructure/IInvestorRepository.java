package com.migration.infrastructure;

import com.migration.domain.Investor;
import jakarta.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

@Resource
public interface IInvestorRepository extends JpaRepository<Investor, Integer> {
}
