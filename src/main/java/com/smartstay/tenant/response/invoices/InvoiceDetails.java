package com.smartstay.tenant.response.invoices;


import com.smartstay.tenant.dto.bills.PaymentHistoryProjection;

import java.util.List;

public record InvoiceDetails(String invoiceNumber, String invoiceId, String invoiceDate, String dueDate, String emailId,
                             String mobile, String countryCode, String hostelId, CustomerInfo customerInfo,
                             StayInfo stayInfo, InvoiceInfo invoiceInfo, AccountDetails accountDetails,
                             List<PaymentHistoryProjection> paymentHistory, ConfigInfo configurations) {
}
