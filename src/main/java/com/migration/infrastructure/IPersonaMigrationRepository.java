package com.migration.infrastructure;

import com.migration.domain.persona.PersonaMigration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPersonaMigrationRepository extends JpaRepository<PersonaMigration, Integer> {

    List<PersonaMigration> findByCpfCnpj (String taxId);
}
