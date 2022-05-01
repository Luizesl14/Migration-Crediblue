package com.migration.infrastructure;

import com.migration.domain.Simulation;
import jakarta.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

@Resource
public interface SimulatonRepository extends JpaRepository<Simulation, Integer> {
}
