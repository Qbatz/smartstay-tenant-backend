package com.smartstay.tenant.response.receipt;

import com.smartstay.tenant.response.invoices.AccountDetails;
import com.smartstay.tenant.response.invoices.CustomerInfo;
import com.smartstay.tenant.response.invoices.StayInfo;

public record ReceiptDetails(
        String invoiceNumber,
        String receiptId,
        String invoiceDate,
        String invoiceId,
        Double invoiceAmount,
        Double paidAmount,
        Double dueAmount,
        String emailId,
        String mobile,
        String countryCode,
        ReceiptInfo receiptInfo,
        CustomerInfo customerInfo,
        StayInfo stayInfo,

        String invoiceRentalPeriod,
        AccountDetails accountDetails,
        ReceiptConfigInfo configurations
) {
}
