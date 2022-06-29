package com.migration.infrastructure;

import com.migration.domain.Partner;
import com.migration.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPartnerRepository extends JpaRepository<Partner, Integer> {

    @Query("FROM Partner p WHERE p.id = :id")
    Partner findByPartnerId(Integer id);

    @Query("FROM Partner p WHERE p.address is not null ")
    List<Partner> findByPartnerAddress();

    @Query("FROM Partner p WHERE p.financialInstitutionCode is not null ")
    List<Partner> findByPartnerAccount();

    @Query("FROM Partner p WHERE p.persona.taxId = :taxId")
    Partner findbyTaxId(String taxId);

    @Query("FROM Partner p WHERE p.cpfCnpj is null")
    List<Partner> findByPartnerTaxIdIsNull();

    @Query("FROM Partner p WHERE p.cpfCnpj is null")
    List<Partner> findByPartnerCpfIsNull();

    @Query(value = "FROM Partner p WHERE (:#{#name} IS NULL OR :#{#name} = '' OR p.name =:#{#name}) " +
            "AND (:#{#email} IS NULL OR :#{#email} = '' OR p.email =:#{#email}) " +
            "AND (:#{#telephone} IS NULL OR :#{#telephone} = '' OR p.telephone =:#{#telephone})" +
            " AND (:#{#cpf} IS NULL OR :#{#cpf} = '' OR p.cpfCnpj =:#{#cpf}) ")
    Partner findPersonaPartner(
            @Param("name") String name,
            @Param("email") String email,
            @Param("telephone") String telephone,
            @Param("cpf") String cpf);
}
