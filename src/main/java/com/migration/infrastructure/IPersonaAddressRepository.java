package com.migration.infrastructure;

import com.migration.domain.persona.aggregation.PersonaAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IPersonaAddressRepository extends JpaRepository<PersonaAddress, Integer> {

    @Query("SELECT count(pa.id) > 0 FROM PersonaAddress pa WHERE pa.data.cep = :cep " +
            "AND pa.data.street = :street AND pa.data.number = :number AND pa.persona.id = :id")
    Boolean existeAddress(String cep, String street, String number, Integer id);
}
