package com.smartstay.tenant.response.invoices;

public record UnpaidInvoices(String invoiceId,
                             String invoiceNo,
                             String invoiceType,
                             Double totalAmount,
                             Double paidAmount,
                             Double balanceAmount) {
}
