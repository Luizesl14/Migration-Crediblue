package com.migration.infrastructure;

import com.migration.domain.persona.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPersonaRepository extends JpaRepository<Persona, Integer> {

    @Query(value = "FROM Persona p WHERE p.taxId = :taxId ")
    Persona findByTaxId(String taxId);

    Persona findByCpfCnpj(String taxId);

    @Query(value = "FROM Persona p WHERE p.cpfCnpj = :taxId ")
    List<Persona> findAllByTaxId(String taxId);

    @Query(value = "FROM Persona p WHERE p.cpfCnpj = :cpfCnpj")
    List<Persona> findByTaxIdOld(@Param("cpfCnpj") String cpfCnpj);

    @Query(value = "FROM Persona p WHERE p.id = :id ")
    Persona findByPersonaId(@Param("id") Integer id);

}
