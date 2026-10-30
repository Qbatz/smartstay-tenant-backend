package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.SettlementItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementItemsRepository extends JpaRepository<SettlementItems, Long> {

    SettlementItems findByInvoiceId(String invoiceId);
}
