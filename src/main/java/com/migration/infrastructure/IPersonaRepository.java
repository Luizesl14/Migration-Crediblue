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
    Persona findAllByTaxId(String taxId);

    @Query(value = "FROM Persona p WHERE p.taxId IS NOT NULL AND p.maritalStatus is not null ")
    List<Persona> findByAllPersonasNormalized();

    Persona findByCpfCnpj(String taxId);

    Persona findByTaxId(String taxId);

    @Query(value = "SELECT DISTINCT p.cpfCnpj FROM Persona p  ")
    List<Persona> findAllByCpfCnpj();

    @Query(value = "FROM Persona p WHERE p.cpfCnpj = :cpfCnpj")
    List<Persona> findByTaxIdOld(@Param("cpfCnpj") String cpfCnpj);

    @Query(value = "FROM Persona p WHERE p.id = :id ")
    Persona findByPersonaId(@Param("id") Integer id);


    @Query(value = "FROM Persona p WHERE (:#{#name} IS NULL OR :#{#name} = '' OR p.name =:#{#name}) " +
            "AND (:#{#email} IS NULL OR :#{#email} = '' OR p.email =:#{#email}) " +
            "AND (:#{#telephone} IS NULL OR :#{#telephone} = '' OR p.telephone =:#{#telephone})" +
            " AND (:#{#cpf} IS NULL OR :#{#cpf} = '' OR p.cpfCnpj =:#{#cpf}) ")
    Persona findPersonaUser(
            @Param("name") String name,
            @Param("email") String email,
            @Param("telephone") String telephone,
            @Param("cpf") String cpf);



    @Query(value = "SELECT  p FROM Persona p WHERE p.leadProposal is not null ")
    List<Persona> personaLeadProposal();

    @Query(value = "SELECT  p FROM Persona p WHERE p.proponentType is null  AND p.taxId is not null")
    List<Persona> personaType();

}
