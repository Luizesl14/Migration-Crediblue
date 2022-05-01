package com.migration.infrastructure;

import com.migration.domain.persona.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IPersonaRepository extends JpaRepository<Persona, Integer> {

    @Query(value = "FROM Persona p WHERE p.taxId = :taxId ")
    Persona findByTaxId(String taxId);
}
