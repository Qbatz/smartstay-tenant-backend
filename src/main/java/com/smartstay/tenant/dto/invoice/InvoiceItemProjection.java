package com.smartstay.tenant.dto.invoice;

import java.util.Date;

public interface InvoiceItemProjection {

    String getInvoiceId();
    String getInvoiceType();
    String getInvoiceNumber();

    Double getTotalAmount();
    Date getInvoiceDueDate();
    Date getInvoiceGeneratedDate();

    Date getInvoiceStartDate();

    Double getPaidAmount();
    Double getDueAmount();

    String getStatus();

    Date getPaidAt();
}
