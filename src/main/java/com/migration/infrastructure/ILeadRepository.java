package com.migration.infrastructure;

import com.migration.domain.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILeadRepository extends JpaRepository<Lead, Integer> {

}
