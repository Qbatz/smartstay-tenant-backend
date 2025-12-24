package com.smartstay.tenant.dto.bills;

public record PaymentSummary(String hostelId,
                             String customerId,
                             String invoiceId,
                             Double amount,
                             String customerMailId,
                             String customerMobile,
                             String customerStatus) {
}
