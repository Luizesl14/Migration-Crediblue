package com.migration.infrastructure;

import com.migration.domain.User;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.Role;
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

    @Query(value =
            "SELECT * FROM credi_user u " +
                    "INNER JOIN credi_user_role ur ON " +
                    "ur.user_id = u.id INNER JOIN credi_role r ON " +
                    "r.id = ur.role_id " +
                    "WHERE r.role = :role ", nativeQuery = true)
    List<User> findByRoleRole(@Param("role") String role);

    @Query("FROM User u WHERE u.cpf is null")
    List<User> findByUserCpfNull();

    @Query("FROM User u WHERE u.cpf = '' ")
    List<User> findByUserIsEmpity();






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
