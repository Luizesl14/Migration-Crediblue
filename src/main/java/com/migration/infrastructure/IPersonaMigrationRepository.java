package com.migration.infrastructure;

import com.migration.domain.persona.Persona;
import com.migration.domain.persona.PersonaMigration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPersonaMigrationRepository extends JpaRepository<PersonaMigration, Integer> {

    @Query(value = "SELECT  p FROM PersonaMigration p WHERE p.taxId = :taxId")
    PersonaMigration findAllByTaxId(String taxId);

    @Query(value = "FROM PersonaMigration p WHERE p.maritalStatus is not null AND p.companion is not null ")
    List<PersonaMigration> findByAllPersonasNormalized();

    @Query(value = "FROM PersonaMigration p WHERE p.leadProposal is null ")
    List<PersonaMigration> findPropeonenSegundariosNormalized();

    @Query(value = "FROM PersonaMigration p WHERE p.leadProposal IS NOT NULL ")
    List<PersonaMigration> findPropeonenmMainNormalized();

    PersonaMigration findByCpfCnpj(String taxId);

    PersonaMigration findByTaxId(String taxId);

    @Query(value = "SELECT DISTINCT p.cpfCnpj FROM PersonaMigration p  ")
    List<PersonaMigration> findAllByCpfCnpj();

    @Query(value = "FROM PersonaMigration p WHERE p.cpfCnpj = :cpfCnpj")
    List<PersonaMigration> findByTaxIdOld(@Param("cpfCnpj") String cpfCnpj);

    @Query(value = "FROM PersonaMigration p WHERE p.id = :id ")
    PersonaMigration findByPersonaId(@Param("id") Integer id);


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
