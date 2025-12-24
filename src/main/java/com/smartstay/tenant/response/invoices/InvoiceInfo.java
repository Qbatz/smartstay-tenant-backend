package com.smartstay.tenant.response.invoices;


import com.smartstay.tenant.dao.Deductions;

import java.util.List;

public record InvoiceInfo(Double subTotal,
                          Double taxAmount,
                          Double taxPercentage,
                          Double totalAmount,
                          Double paidAmount,
                          Double balanceAmount,
                          String invoicePeriod,
                          String invoiceMonth,
                          String paymentStatus,
                          boolean isCancelled,
                          double totalDeduction,
                          List<InvoiceItems> invoiceItems,
                          List<Deductions> listDeductions) {
}
