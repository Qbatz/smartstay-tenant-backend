package com.smartstay.tenant.response.invoices;

public record RedemptionInfoRes(String invoiceId,
                                String invoiceNumber,
                                Double invoiceAmount,
                                String invoiceDate,
                                String invoiceType,
                                String defaultInvoiceType,
                                String redeemedDate,
                                Double redeemedAmount) {
}
