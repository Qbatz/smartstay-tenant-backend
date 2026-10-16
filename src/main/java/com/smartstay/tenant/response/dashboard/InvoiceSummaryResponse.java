package com.smartstay.tenant.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceSummaryResponse {

    private Double rent;
    private Double eb;
    private Double discountAmount;
    private Double paidAmount;
    private String invoiceNumber;
    private String invoiceGeneratedDate;
    private String invoiceDueDate;
    private String currentInvoiceStartDate;
    private String currentInvoiceEndDate;
    private String paymentStatus;
    private String paymentDate;
}

