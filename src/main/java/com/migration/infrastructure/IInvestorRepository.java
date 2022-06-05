package com.migration.infrastructure;

import com.migration.domain.Investor;
import jakarta.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Resource
public interface IInvestorRepository extends JpaRepository<Investor, Integer> {
    @Query("FROM Investor i WHERE i.id = :id")
    Investor findByInvestorId(Integer id);
}
