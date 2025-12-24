package com.smartstay.tenant.dto.bills;

public interface PaymentHistoryProjection {
    String getReferenceNumber();
    Double getAmount();
    String getPaidDate();
    String getTransactionReferenceId();
}
