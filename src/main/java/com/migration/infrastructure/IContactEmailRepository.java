package com.migration.infrastructure;

import com.migration.domain.persona.aggregation.ContactEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IContactEmailRepository extends JpaRepository<ContactEmail, Integer> {

    @Query("SELECT ce FROM ContactEmail ce WHERE ce.email = :email")
    ContactEmail findContactEmailsByEmail(@Param("email") String email);
}
