package com.smartstay.tenant.dto.invoice;

import java.time.LocalDate;

public interface InvoiceSummaryProjection {

    Double getRentAmount();

    Double getEbAmount();

    Double getPaidAmount();

    String getInvoiceNumber();

    LocalDate getInvoiceGeneratedDate();

    LocalDate getInvoiceDueDate();

    LocalDate getInvoiceStartDate();

    LocalDate getInvoiceEndDate();
}
