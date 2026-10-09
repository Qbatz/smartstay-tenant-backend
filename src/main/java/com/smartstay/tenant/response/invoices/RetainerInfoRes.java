package com.smartstay.tenant.response.invoices;

public record RetainerInfoRes(String invoiceId,
                              String invoiceNumber,
                              String invoiceDate,
                              Double invoiceAmount,
                              Double redeemedAmount,
                              Double balanceAmount) {
}
