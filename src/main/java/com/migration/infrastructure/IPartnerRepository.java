package com.migration.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPartnerRepository extends JpaRepository<Object, Integer> {
}
