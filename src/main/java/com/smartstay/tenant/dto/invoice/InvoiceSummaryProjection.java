package com.smartstay.tenant.dto.invoice;

import java.util.Date;

public interface InvoiceSummaryProjection {

    Double getRentAmount();
    Double getEbAmount();
    Double getPaidAmount();

    String getInvoiceNumber();
    Date getInvoiceGeneratedDate();
    Date getInvoiceDueDate();
    Date getInvoiceStartDate();
    Date getInvoiceEndDate();
}
