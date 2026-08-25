package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.KYCUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KycUsageRepository extends JpaRepository<KYCUsage, Long> {

    KYCUsage findByLatestRequestTo(String customerId);
}
