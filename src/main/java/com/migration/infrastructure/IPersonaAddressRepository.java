package com.migration.infrastructure;

import com.migration.domain.persona.aggregation.PersonaAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPersonaAddressRepository extends JpaRepository<PersonaAddress, Integer> {

    @Query("FROM PersonaAddress pa WHERE pa.data.cep = :cep " +
            "AND pa.data.street = :street AND pa.data.number = :number AND pa.persona.id = :id")
    List<PersonaAddress> existeAddress(String cep, String street, String number, Integer id);
}
