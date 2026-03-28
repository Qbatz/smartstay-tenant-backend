package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.InvoiceDiscounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceDiscountsRepository extends JpaRepository<InvoiceDiscounts, Long> {

    @Query("""
            select sum(coalesce(id.discountAmount, 0))
            from InvoiceDiscounts id
            where id.invoiceId = :invoiceId
            group by id.invoiceId
            """)
    Double findDiscountAmountByInvoiceId(@Param("invoiceId") String invoiceId);
}
