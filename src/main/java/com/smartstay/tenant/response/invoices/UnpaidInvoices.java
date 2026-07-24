package com.smartstay.tenant.response.invoices;

public record UnpaidInvoices(String invoiceId,
                             String invoiceNo,
                             String invoiceType,
                             Double totalAmount,
                             Double paidAmount,
                             Double balanceAmount,
                             String cancelledDate,
                             String generatedDate,
                             String createdAtDate,
                             String createdAtTime) {
}
