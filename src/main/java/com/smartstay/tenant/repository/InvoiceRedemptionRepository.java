package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.InvoiceRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRedemptionRepository extends JpaRepository<InvoiceRedemption, Long> {

    @Query("""
                SELECT ir
                FROM InvoiceRedemption ir
                WHERE (
                    ir.sourceInvoiceId = :invoiceId
                    OR ir.targetInvoiceId = :invoiceId
                )
                AND ir.isActive = true
                ORDER BY ir.id DESC
            """)
    List<InvoiceRedemption> findByInvoiceId(@Param("invoiceId") String invoiceId);
}
