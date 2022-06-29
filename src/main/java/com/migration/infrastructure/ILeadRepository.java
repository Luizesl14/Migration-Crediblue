package com.migration.infrastructure;

import com.migration.domain.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ILeadRepository extends JpaRepository<Lead, Integer> {

    @Query("FROM Lead l WHERE  l.email is null")
    List<Lead> findLeadEmailIsNull();

    @Query("FROM Lead l WHERE  l.persona.taxId =:taxId")
    Lead findLeadTaxId(String taxId);

}
