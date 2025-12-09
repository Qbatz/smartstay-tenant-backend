package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.InvoicesV1;
import com.smartstay.tenant.dto.invoice.InvoiceItemDTO;
import com.smartstay.tenant.dto.invoice.InvoiceItemResponseDTO;
import com.smartstay.tenant.response.dashboard.InvoiceSummaryResponse;
import com.smartstay.tenant.response.hostel.InvoiceItems;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface InvoicesV1Repository extends JpaRepository<InvoicesV1, String> {

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
                  AND i.hostelId = :hostelId
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
    List<InvoiceSummaryResponse> getInvoiceSummary(@Param("hostelId") String hostelId, @Param("customerId") String customerId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);


    @Query("""
                SELECT new com.smartstay.tenant.dto.invoice.InvoiceItemResponseDTO(
                    i.invoiceId,
                    i.invoiceType,
                    i.invoiceNumber,
                    i.totalAmount,                             
                    i.invoiceDueDate,
                    i.invoiceGeneratedDate,                         
                    COALESCE(SUM(t.paidAmount), 0.0),       
                    i.totalAmount - COALESCE(SUM(t.paidAmount), 0.0),  
                    CASE 
                        WHEN i.totalAmount - COALESCE(SUM(t.paidAmount), 0) = 0 THEN 'Paid'
                        WHEN COALESCE(SUM(t.paidAmount), 0) = 0 THEN 'Pending'
                        ELSE 'Partially Paid'
                    END
                )
                FROM InvoicesV1 i
                LEFT JOIN TransactionV1 t ON t.invoiceId = i.invoiceId
                WHERE i.hostelId = :hostelId
                  AND i.customerId = :customerId
                  AND i.isCancelled = false
                GROUP BY i.invoiceId
                ORDER BY i.invoiceGeneratedDate DESC
            """)
    List<InvoiceItemResponseDTO> getAllInvoiceItems(@Param("hostelId") String hostelId, @Param("customerId") String customerId);

    @Query("""
                SELECT i 
                FROM InvoicesV1 i
                WHERE i.invoiceId = :invoiceId
                  AND i.customerId = :customerId
                  AND i.isCancelled = false
            """)
    InvoicesV1 getInvoiceByIdAndCustomerId(@Param("invoiceId") String invoiceId, @Param("customerId") String customerId);


    @Query("""
                SELECT new com.smartstay.tenant.dto.invoice.InvoiceItemDTO(ii.amount, ii.invoiceItem)
                FROM InvoiceItems ii
                WHERE ii.invoice.invoiceId = :invoiceId
                  AND (ii.invoiceItem IS NULL OR ii.invoiceItem <> 'EB')
            """)
    List<InvoiceItemDTO> getInvoiceItems(@Param("invoiceId") String invoiceId);


    @Query("SELECT SUM(i.paidAmount) FROM InvoicesV1 i WHERE i.customerId = :customerId AND i.invoiceType = 'ADVANCE'")
    Double findAdvancePaidAmount(@Param("customerId") String customerId);

    @Query("SELECT i FROM InvoicesV1 i WHERE DATE(i.invoiceGeneratedDate) = CURRENT_DATE")
    List<InvoicesV1> findInvoicesGeneratedToday();

//    @Query("""
//        SELECT i
//        FROM InvoicesV1 i
//        JOIN Customers c ON c.customerId = i.customerId
//        JOIN CustomerCredentials cc ON cc.xuid = c.xuid
//        WHERE DATE(i.invoiceGeneratedDate) = CURRENT_DATE
//          AND i.isCancelled = false AND c.customerId = '7e994f6a-2031-47e0-abb9-410f3ecb9efd'
//    """)
//    List<InvoicesV1> findInvoicesGeneratedTodayForActiveCustomers();

    @Query("""
        SELECT i
        FROM InvoicesV1 i
        JOIN Customers c ON c.customerId = i.customerId
        JOIN CustomerCredentials cc ON cc.xuid = c.xuid
        WHERE DATE(i.invoiceGeneratedDate) = CURRENT_DATE
          AND i.isCancelled = false AND i.invoiceType = 'RENT' AND i.invoiceMode = 'RECURRING'
    """)
    List<InvoicesV1> findInvoicesGeneratedTodayForActiveCustomers();

}
