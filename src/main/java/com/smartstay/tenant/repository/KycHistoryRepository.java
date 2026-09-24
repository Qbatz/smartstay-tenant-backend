package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.KycHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KycHistoryRepository extends JpaRepository<KycHistory, Long> {

    KycHistory findTopByHostelIdOrderByHistoryIdDesc(String hostelId);
}
