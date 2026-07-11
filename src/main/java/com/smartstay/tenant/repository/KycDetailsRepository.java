package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.KycDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KycDetailsRepository extends JpaRepository<KycDetails, Long> {
}
