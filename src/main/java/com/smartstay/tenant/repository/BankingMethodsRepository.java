package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.BankingMethods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankingMethodsRepository extends JpaRepository<BankingMethods, String> {

    BankingMethods findByPaymentMethodId(String paymentMethodId);
}
