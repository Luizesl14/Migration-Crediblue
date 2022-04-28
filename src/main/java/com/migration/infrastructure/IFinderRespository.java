package com.migration.infrastructure;

import com.migration.domain.Finder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFinderRespository extends JpaRepository<Finder, Integer> {
}
