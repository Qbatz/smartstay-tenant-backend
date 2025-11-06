package com.smartstay.tenant.dto;


public record InvoiceItemDTO(
        String invoiceItem,
        String otherItem,
        Double amount
) {
}
