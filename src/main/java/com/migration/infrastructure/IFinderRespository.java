package com.migration.infrastructure;

import com.migration.domain.Finder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFinderRespository extends JpaRepository<Finder, Integer> {

    @Query("FROM Finder f WHERE f.persona.taxId = :taxId ")
    Finder findByTaxId(String taxId);

    @Query("FROM Finder f WHERE f.name is not null")
    List<Finder> findNameIsNotNull();

    @Query("FROM Finder f WHERE f.address IS NOT NULL")
    List<Finder> findNameAddressIsNotNull();

    @Query("FROM Finder f WHERE f.name IS  NULL")
    List<Finder> findNameNameIsNull();

    @Query("FROM Finder f WHERE f.financialInstitutionCode is not null")
    List<Finder> findNameAccountIsNotNull();
}
