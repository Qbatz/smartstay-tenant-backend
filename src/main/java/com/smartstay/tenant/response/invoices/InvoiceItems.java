package com.smartstay.tenant.response.invoices;

public record InvoiceItems(String invoiceNo,
                           String description,
                           Double amount) {
}
