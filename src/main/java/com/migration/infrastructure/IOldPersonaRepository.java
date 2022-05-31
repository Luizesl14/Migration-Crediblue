package com.migration.infrastructure;

import com.migration.domain.persona.OldPersona;
import com.migration.domain.persona.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IOldPersonaRepository extends JpaRepository<OldPersona, Integer> {

    List<OldPersona> findByCpfCnpj (String taxId);
}
