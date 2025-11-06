package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.InvoicesV1;
import com.smartstay.tenant.response.hostel.InvoiceItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface InvoicesV1Repository extends JpaRepository<InvoicesV1, String> {

    @Query("SELECT ii.invoiceItem AS invoiceItem, " +
            "ii.amount AS amount, " +
            "COALESCE(t.paidAt, i.invoiceGeneratedDate) AS paidDate, " +
            "COALESCE(t.paidAmount, 0) AS paidAmount " +
            "FROM InvoicesV1 i " +
            "JOIN i.invoiceItems ii " +
            "LEFT JOIN TransactionV1 t ON i.invoiceId = t.invoiceId " +
            "WHERE i.customerId = :customerId " +
            "AND i.invoiceGeneratedDate BETWEEN :startDate AND :endDate")
    List<InvoiceItems> findInvoiceItemsWithTransactionsByCustomerAndDateRange(
            @Param("customerId") String customerId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );
}
