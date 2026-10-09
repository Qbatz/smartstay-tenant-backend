package com.smartstay.tenant.response.invoices;

public record AdditionalAdvanceItems(String invoiceId,
                                     String invoiceNumber,
                                     Double amount) {
}
