package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.BankingV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankingV2Repository extends JpaRepository<BankingV2, String> {

    BankingV2 findByBankIdAndIsActiveTrueAndIsDeletedFalse(String bankId);
}
