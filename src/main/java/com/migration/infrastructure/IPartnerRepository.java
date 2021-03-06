package com.migration.infrastructure;

import com.migration.domain.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPartnerRepository extends JpaRepository<Partner, Integer> {
}
