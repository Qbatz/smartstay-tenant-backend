package com.smartstay.tenant.dto.invoice;

import java.util.List;

public record AdvanceInfo(Double advanceAmount,
                          Double advancePaid,
                          String advanceInvoiceNumber,
                          Double bookingAmount,
                          Double totalAdvancePaid,
                          List<Deductions> deductions) {
}
