package com.migration.infrastructure;

import com.migration.domain.User;
import com.migration.domain.persona.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserRepository extends JpaRepository<User, Integer> {

    @Query("FROM User u WHERE u.cpf is null")
    List<User> findByUserTaxIdIsNull();



    @Query(value = "FROM User u WHERE u.name =:#{#name} AND u.email = :#{#email} " +
            "AND u.telephone = :#{#telephone} AND (:#{#cpf} IS NULL OR :#{#cpf} = '' OR u.cpf =:#{#cpf}) ")
    User findPersonaUser(
            @Param("name") String name,
            @Param("email") String email,
            @Param("telephone") String telephone,
            @Param("cpf") String cpf);
}
