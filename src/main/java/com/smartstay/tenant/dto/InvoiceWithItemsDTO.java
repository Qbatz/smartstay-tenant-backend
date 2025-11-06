package com.smartstay.tenant.dto;

import java.util.Date;
import java.util.List;

public record InvoiceWithItemsDTO(
        String invoiceId,
        String customerId,
        String invoiceNumber,
        Double totalAmount,
        Date invoiceGeneratedDate,
        List<InvoiceItemDTO> items
) {
}

