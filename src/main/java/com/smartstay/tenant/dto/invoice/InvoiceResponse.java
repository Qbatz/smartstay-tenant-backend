package com.smartstay.tenant.dto.invoice;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponse {

    private String invoiceId;
    private String invoiceItem;
    private Double amount;
}
