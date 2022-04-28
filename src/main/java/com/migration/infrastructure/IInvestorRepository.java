package com.migration.infrastructure;

import jakarta.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

@Resource
public interface IInvestorRepository extends JpaRepository<Object, Integer> {
}
