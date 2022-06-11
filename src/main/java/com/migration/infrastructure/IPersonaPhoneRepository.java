package com.migration.infrastructure;

import com.migration.domain.persona.aggregation.PersonaPhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPersonaPhoneRepository extends JpaRepository<PersonaPhone, Integer> {
    @Query("FROM PersonaPhone pp WHERE pp.phone.number = :number AND pp.persona.id = :id ")
    List<PersonaPhone> existsPhone(String number, Integer id);
}
