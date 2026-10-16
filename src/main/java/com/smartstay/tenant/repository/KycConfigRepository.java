package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.KycConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KycConfigRepository extends JpaRepository<KycConfig, Long> {

    KycConfig findByHostelId(String hostelId);
}
