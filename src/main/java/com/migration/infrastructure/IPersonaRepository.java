package com.migration.infrastructure;

import com.migration.domain.persona.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPersonaRepository extends JpaRepository<Persona, Integer> {

    @Query(value = "SELECT DISTINCT p FROM Persona p WHERE p.taxId = :taxId ")
    List<Persona> findAllByTaxId(String taxId);

    @Query(value = "FROM Persona p WHERE p.taxId IS NOT NULL ")
    List<Persona> findByAllPersonasNormalized();

    Persona findByCpfCnpj(String taxId);

    Persona findByTaxId(String taxId);

    @Query(value = "SELECT DISTINCT p.cpfCnpj FROM Persona p  ")
    List<Persona> findAllByCpfCnpj();

    @Query(value = "FROM Persona p WHERE p.cpfCnpj = :cpfCnpj")
    List<Persona> findByTaxIdOld(@Param("cpfCnpj") String cpfCnpj);

    @Query(value = "FROM Persona p WHERE p.id = :id ")
    Persona findByPersonaId(@Param("id") Integer id);



    @Query(value = "FROM Persona p WHERE p.name =:#{#name} AND p.email = :#{#email} " +
            "AND p.telephone = :#{#telephone} AND (:#{#cpfCnpj} IS NULL OR :#{#cpfCnpj} = '' OR p.cpfCnpj =:#{#cpfCnpj}) ")
    Persona findPersonaUser(
            @Param("name") String name,
            @Param("email") String email,
            @Param("telephone") String telephone,
            @Param("cpfCnpj") String cpfCnpj);


    @Query(value = "SELECT  p FROM Persona p WHERE p.leadProposal is not null ")
    List<Persona> personaLeadProposal();

}
