package com.migration.infrastructure;

import com.migration.domain.persona.aggregation.PersonaAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPersonaAddressRepository extends JpaRepository<PersonaAddress, Integer> {
}
