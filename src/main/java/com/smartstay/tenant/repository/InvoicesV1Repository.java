package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.InvoicesV1;
import com.smartstay.tenant.dto.invoice.InvoiceItemDTO;
import com.smartstay.tenant.dto.invoice.InvoiceItemProjection;
import com.smartstay.tenant.dto.invoice.InvoiceSummaryProjection;
import com.smartstay.tenant.response.dashboard.InvoiceSummaryResponse;
import com.smartstay.tenant.response.hostel.InvoiceItems;
import com.smartstay.tenant.response.invoices.InvoiceSummary;
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


    @Query(value = """
            SELECT
                SUM(CASE WHEN ii.invoice_item = 'RENT' THEN ii.amount ELSE 0 END) AS rentAmount,
                SUM(CASE WHEN ii.invoice_item = 'EB' THEN ii.amount ELSE 0 END) AS ebAmount,
                SUM(COALESCE(t.paid_amount, 0)) AS paidAmount,
                i.invoice_number AS invoiceNumber,
                i.invoice_generated_date AS invoiceGeneratedDate,
                i.invoice_due_date AS invoiceDueDate,
                i.invoice_start_date AS invoiceStartDate,
                i.invoice_end_date AS invoiceEndDate
            FROM invoicesv1 i
            JOIN invoice_items ii ON ii.invoice_id = i.invoice_id
            LEFT JOIN transactionv1 t ON t.invoice_id = i.invoice_id
            WHERE i.customer_id = :customerId
              AND i.hostel_id = :hostelId
             AND DATE(i.invoice_start_date) >= DATE(:startDate)
             AND DATE(i.invoice_start_date) <= DATE(:endDate)
            GROUP BY
                i.invoice_id,
                i.invoice_number,
                i.invoice_generated_date,
                i.invoice_due_date,
                i.invoice_start_date,
                i.invoice_end_date
            ORDER BY i.invoice_start_date DESC
            LIMIT 1
            """, nativeQuery = true)
    InvoiceSummaryProjection getInvoiceSummary(@Param("hostelId") String hostelId, @Param("customerId") String customerId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

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


    @Query(value = """
            SELECT
                i.invoice_id            AS invoiceId,
                i.invoice_type          AS invoiceType,
                i.invoice_number        AS invoiceNumber,
                i.total_amount          AS totalAmount,
                i.invoice_due_date      AS invoiceDueDate,
                i.invoice_generated_date AS invoiceGeneratedDate,
                i.invoice_start_date    AS invoiceStartDate,
                COALESCE(SUM(t.paid_amount), 0) AS paidAmount,
                (i.total_amount - COALESCE(SUM(t.paid_amount), 0)) AS dueAmount,
                i.payment_status        AS status,
                i.paid_at         AS paymentDate
            FROM invoicesv1 i
            LEFT JOIN transactionv1 t
                   ON t.invoice_id = i.invoice_id
            WHERE i.hostel_id = :hostelId
              AND i.customer_id = :customerId
              AND i.is_cancelled = false
            GROUP BY
                i.invoice_id,
                i.invoice_type,
                i.invoice_number,
                i.total_amount,
                i.invoice_due_date,
                i.invoice_generated_date
            ORDER BY i.invoice_generated_date DESC
            """, nativeQuery = true)
    List<InvoiceItemProjection> getAllInvoiceItems(@Param("hostelId") String hostelId, @Param("customerId") String customerId);


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

    @Query("""
            SELECT inv FROM InvoicesV1 inv WHERE inv.customerId=:customerId AND inv.hostelId=:hostelId AND
            inv.invoiceType='ADVANCE'
            """)
    InvoicesV1 findAdvanceInvoice(String customerId, String hostelId);

    @Query("""
            SELECT inv FROM InvoicesV1 inv WHERE inv.customerId=:customerId AND inv.hostelId=:hostelId AND
            inv.invoiceType='BOOKING'
            """)
    InvoicesV1 findBookingInvoice(String customerId, String hostelId);

    @Query("""
            SELECT inv from InvoicesV1 inv WHERE inv.customerId=:customerId AND DATE(inv.invoiceStartDate) <= DATE(:endDate) 
            AND DATE(inv.invoiceEndDate) >= DATE(:startDate) AND inv.invoiceType in ('RENT', 'REASSIGN_RENT')
            """)
    List<InvoicesV1> findCurrentMonthInvoices(String customerId, Date startDate, Date endDate);


    @Query(
            value = """
        SELECT 
            i.invoice_number AS invoiceNumber,
            i.total_amount AS totalAmount,
            DATE_FORMAT(i.invoice_start_date, '%d/%m/%Y') AS invoiceStartDate,
            i.invoice_type AS invoiceType
        FROM invoicesv1 i
        WHERE i.hostel_id = :hostelId
        AND i.invoice_id IN (:invoiceId)
        """,
            nativeQuery = true
    )
    List<InvoiceSummary> findInvoiceSummariesByHostelId(
            @Param("hostelId") String hostelId,
            @Param("invoiceId") List<String> invoiceId
    );

    @Query(value = """
            SELECT * FROM `invoicesv1` WHERE customer_id=:customerId AND hostel_id=:hostelId AND DATE(invoice_start_date) >= DATE(:startDate) 
             AND  (invoice_type='RENT' OR invoice_type='REASSIGN_RENT')
            """, nativeQuery = true)
    List<InvoicesV1> findAllCurrentMonthInvoices(@Param("customerId") String customerId, @Param("hostelId") String hostelId, @Param("startDate") Date startDate);

    @Query(value = """
            SELECT * FROM invoicesv1 invc WHERE invc.customer_id=:customerId and DATE(invc.invoice_start_date) >= DATE(:startDate) ORDER BY invc.invoice_start_date DESC LIMIT 1;
            """, nativeQuery = true)
    InvoicesV1 findCurrentRunningInvoice(@Param("customerId") String customerId, @Param("startDate") Date startDate);


    @Query(value = """
        SELECT COALESCE(SUM(paid_amount), 0)
        FROM invoicesv1
        WHERE customer_id = :customerId
          AND hostel_id = :hostelId
          AND DATE(invoice_start_date) >= DATE(:startDate)
          AND invoice_type IN ('RENT', 'REASSIGN_RENT')
        """, nativeQuery = true)
    Double getTotalPaidAmountForCurrentMonth(
            @Param("customerId") String customerId,
            @Param("hostelId") String hostelId,
            @Param("startDate") Date startDate
    );

}
