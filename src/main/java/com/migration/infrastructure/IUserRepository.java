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

    @Query("FROM User u WHERE u.partner is null  AND  u.investor is null")
    List<User> findByUserCpfAndCnpjNull();

    @Query("FROM User u WHERE u.persona.taxId =:taxId")
    User findByUserTaxId(String taxId);

    @Query("FROM User u WHERE u.partner is not null AND u.persona is null")
    List<User> findByUserPartner();

    @Query("FROM User u WHERE u.investor is not null AND u.persona is null ")
    List<User> findByUserInvestor();




    @Query(value = "FROM User u WHERE (:#{#name} IS NULL OR :#{#name} = '' OR u.name =:#{#name}) " +
            "AND (:#{#email} IS NULL OR :#{#email} = '' OR u.email =:#{#email}) " +
            "AND (:#{#telephone} IS NULL OR :#{#telephone} = '' OR u.telephone =:#{#telephone})" +
            " AND (:#{#cpf} IS NULL OR :#{#cpf} = '' OR u.cpf =:#{#cpf}) ")
    User findPersonaUser(
            @Param("name") String name,
            @Param("email") String email,
            @Param("telephone") String telephone,
            @Param("cpf") String cpf);
}
