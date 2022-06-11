package com.migration.infrastructure;

import com.migration.domain.persona.aggregation.PersonaAccounts;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPersonaAccountRepository extends JpaRepository<PersonaAccounts, Integer> {
    @Query("SELECT pba FROM  PersonaAccounts pba WHERE  pba.persona.cpfCnpj = :taxId")
    List<PersonaAccounts> findByBankAcccount(@Param("taxId") String taxId);

    @Query("FROM PersonaAccounts pba WHERE pba.account.financialInstitutionCode = :code " +
            "AND pba.account.accountBranch = :ag AND  pba.account.accountNumber = :cc AND pba.persona.id =:id")
    List<PersonaAccounts> existsAccount(String code, String ag, String cc, Integer id);
}
