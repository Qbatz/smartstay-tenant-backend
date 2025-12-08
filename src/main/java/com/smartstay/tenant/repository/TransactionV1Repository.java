package com.smartstay.tenant.repository;


import com.smartstay.tenant.dao.TransactionV1;
import com.smartstay.tenant.dto.invoice.ReceiptDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionV1Repository extends JpaRepository<TransactionV1, String> {

    List<TransactionV1> findByCustomerIdAndHostelId(String customerId, String hostelId);

    @Query("""
                SELECT new com.smartstay.tenant.dto.invoice.ReceiptDTO(
                    t.transactionId,
                    t.referenceNumber,
                    b.accountType,
                    b.bankName,
                    t.paidAmount,
                    t.paidAt
                )
                FROM TransactionV1 t
                LEFT JOIN BankingV1 b ON b.bankId = t.bankId
                WHERE t.invoiceId = :invoiceId
                ORDER BY t.paidAt DESC
            """)
    List<ReceiptDTO> getReceiptsByInvoiceId(@Param("invoiceId") String invoiceId);

    @Query("""
                SELECT COALESCE(SUM(t.paidAmount), 0)
                FROM TransactionV1 t
                WHERE t.invoiceId = :invoiceId
            """)
    Double getTotalPaid(@Param("invoiceId") String invoiceId);


    @Query("""
       SELECT t FROM TransactionV1 t 
       WHERE t.invoiceId = :invoiceId 
       ORDER BY t.paymentDate DESC
       """)
    TransactionV1 findLatestTransaction(String invoiceId);


}
