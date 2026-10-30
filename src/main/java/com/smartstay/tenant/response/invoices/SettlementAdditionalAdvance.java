package com.smartstay.tenant.response.invoices;

import java.util.List;

public record SettlementAdditionalAdvance(int totalInvoice,
                                          Double invoiceAmount,
                                          Double balanceAmount,
                                          List<AdditionalAdvanceItems> additionalAdvanceItems) {
}
