package com.smartstay.tenant.response.invoices;

public record UnpaidInvoiceItem(String invoiceNumber,
                                Double invoiceAmount,
                                Double paidAmount,
                                Double pendingAmount) {
}
