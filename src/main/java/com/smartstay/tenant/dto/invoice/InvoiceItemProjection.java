package com.smartstay.tenant.dto.invoice;

import java.time.LocalDate;
import java.util.Date;

public interface InvoiceItemProjection {

    String getInvoiceId();
    String getInvoiceType();
    String getInvoiceNumber();

    Double getTotalAmount();
    LocalDate getInvoiceDueDate();
    LocalDate getInvoiceGeneratedDate();

    LocalDate getInvoiceStartDate();

    Double getPaidAmount();
    Double getDueAmount();

    String getStatus();

    LocalDate getPaidAt();

    LocalDate getPaymentDate();

    Boolean getIsCancelled();
}
