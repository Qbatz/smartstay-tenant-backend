package com.smartstay.tenant.response.invoices;

import java.util.List;

public record UnpaidInvoiceInfo(int noOfUnpaidInvoices,
                                Double unpaidInvoicesTotalPendingAmount,
                                List<UnpaidInvoiceItem> unpaidInvoiceItems) {
}
