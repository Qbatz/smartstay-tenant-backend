package com.smartstay.tenant.response.receipt;

public record ReceiptInfo(String receiptNumber,
                          String receiptId,
                          String transactionDate,
                          String transactionTime,
                          Double paidAmount,
                          String particulars,
                          String transactionId,
                          String receivedBy,
                          String invoiceMonth,
                          String paymentMode
                          ) {
}
