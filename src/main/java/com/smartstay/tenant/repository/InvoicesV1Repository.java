package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.InvoicesV1;
import com.smartstay.tenant.dto.InvoiceItemResponseDTO;
import com.smartstay.tenant.response.dashboard.InvoiceSummaryResponse;
import com.smartstay.tenant.response.hostel.InvoiceItems;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface InvoicesV1Repository extends JpaRepository<InvoicesV1, String> {

    @Query("SELECT ii.invoiceItem AS invoiceItem, " + "ii.amount AS amount, " + "COALESCE(t.paidAt, i.invoiceGeneratedDate) AS paidDate, " + "COALESCE(t.paidAmount, 0) AS paidAmount " + "FROM InvoicesV1 i " + "JOIN i.invoiceItems ii " + "LEFT JOIN TransactionV1 t ON i.invoiceId = t.invoiceId " + "WHERE i.customerId = :customerId " + "AND i.invoiceGeneratedDate BETWEEN :startDate AND :endDate")
    List<InvoiceItems> findInvoiceItemsWithTransactionsByCustomerAndDateRange(@Param("customerId") String customerId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("""
            SELECT new com.smartstay.tenant.response.hostel.InvoiceItems(
                   i.invoiceId,
                   i.invoiceNumber,
                   i.invoiceType,
                   i.invoiceGeneratedDate,
                   i.invoiceDueDate,
                   ii.invoiceItem,
                   SUM(ii.amount),
                   SUM(COALESCE(t.paidAmount, 0)),
                   MAX(COALESCE(t.paidAt, i.invoiceGeneratedDate))
            )
            FROM InvoicesV1 i
            JOIN i.invoiceItems ii
            LEFT JOIN TransactionV1 t ON t.invoiceId = i.invoiceId
            WHERE i.customerId = :customerId
              AND ii.invoiceItem IN (:itemTypes)
              AND i.invoiceGeneratedDate BETWEEN :startDate AND :endDate
            GROUP BY 
                   i.invoiceId,
                   i.invoiceNumber,
                   i.invoiceType,
                   i.invoiceGeneratedDate,
                   i.invoiceDueDate,
                   ii.invoiceItem
            ORDER BY i.invoiceGeneratedDate DESC
            """)
    List<InvoiceItems> getInvoiceItemDetails(@Param("customerId") String customerId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("itemTypes") List<String> itemTypes);


    @Query("""
            SELECT new com.smartstay.tenant.dto.InvoiceItemResponseDTO(
               i.invoiceId,
               i.invoiceType,
               i.invoiceNumber,
               ii.amount,
               i.paymentStatus,
               i.invoiceDueDate,
               i.invoiceGeneratedDate,
               ii.invoiceItem
            )
            FROM InvoicesV1 i
            LEFT JOIN i.invoiceItems ii
            WHERE i.hostelId = :hostelId
              AND i.customerId = :customerId
              AND i.isCancelled = false
            ORDER BY i.invoiceGeneratedDate DESC
            """)
    List<InvoiceItemResponseDTO> getAllInvoiceItems(@Param("hostelId") String hostelId, @Param("customerId") String customerId);


    @Query("""
            SELECT new com.smartstay.tenant.dto.InvoiceItemResponseDTO(
               i.invoiceId,
               i.invoiceType,
               i.invoiceNumber,
               ii.amount,
               i.paymentStatus,
               i.invoiceDueDate,
               i.invoiceGeneratedDate,
               ii.invoiceItem
            )
            FROM InvoicesV1 i
            LEFT JOIN i.invoiceItems ii
            WHERE i.invoiceId = :invoiceId
              AND i.hostelId = :hostelId
              AND i.customerId = :customerId
              AND i.isCancelled = false
            ORDER BY i.invoiceGeneratedDate DESC
            """)
    List<InvoiceItemResponseDTO> getInvoiceDetails(@Param("invoiceId") String invoiceId, @Param("hostelId") String hostelId, @Param("customerId") String customerId);


    @Query("""
                SELECT new com.smartstay.tenant.response.dashboard.InvoiceSummaryResponse(
                    SUM(CASE WHEN ii.invoiceItem = 'RENT' THEN ii.amount ELSE 0 END),
                    SUM(CASE WHEN ii.invoiceItem = 'EB' THEN ii.amount ELSE 0 END),
                    SUM(COALESCE(t.paidAmount, 0)),
                    i.invoiceNumber,
                    i.invoiceGeneratedDate,
                    i.invoiceDueDate,
                    i.invoiceStartDate,
                    i.invoiceEndDate
                )
                FROM InvoicesV1 i
                JOIN i.invoiceItems ii
                LEFT JOIN TransactionV1 t ON t.invoiceId = i.invoiceId
                WHERE i.customerId = :customerId
                  AND i.invoiceGeneratedDate BETWEEN :startDate AND :endDate
                GROUP BY 
                    i.invoiceId,
                    i.invoiceNumber,
                    i.invoiceGeneratedDate,
                    i.invoiceDueDate,
                    i.invoiceStartDate,
                    i.invoiceEndDate
                ORDER BY i.invoiceGeneratedDate DESC
            """)
    List<InvoiceSummaryResponse> getInvoiceSummary(@Param("customerId") String customerId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);


}
