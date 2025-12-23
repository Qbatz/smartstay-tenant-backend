package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.BankingV1;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankingV1Repository extends JpaRepository<BankingV1, String> {

    BankingV1 findByBankId(String bankId);
}
