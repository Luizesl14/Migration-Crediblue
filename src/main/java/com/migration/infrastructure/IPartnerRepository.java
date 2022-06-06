package com.migration.infrastructure;

import com.migration.domain.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPartnerRepository extends JpaRepository<Partner, Integer> {

    @Query("FROM Partner p WHERE p.id = :id")
    Partner findByPartnerId(Integer id);

    @Query("FROM Partner p WHERE p.cpfCnpj is null")
    List<Partner> findByPartnerTaxIdIsNull();
}
