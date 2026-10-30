package com.smartstay.tenant.response.invoices;

import java.util.List;

public record CustomerBookingInfoRes(String label,
                                     Double totalAmount,
                                     Double paidAmount,
                                     Double availableBalance,
                                     Double appliedAmount,
                                     String invoiceNo,
                                     List<RedemptionInfoRes> RedeemedTo) {
}
