package com.migration.infrastructure;

import com.migration.domain.persona.aggregation.PersonaPhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPersonaPhoneRepository extends JpaRepository<PersonaPhone, Integer> {
}
