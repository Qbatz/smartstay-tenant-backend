package com.smartstay.tenant.response.invoices;

public interface InvoiceSummary {
    String getInvoiceNumber();

    Double getTotalAmount();

    String getInvoiceStartDate();

    String getInvoiceType();

}
