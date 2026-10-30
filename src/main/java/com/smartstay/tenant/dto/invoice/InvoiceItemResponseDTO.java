package com.smartstay.tenant.dto.invoice;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceItemResponseDTO {

    private String invoiceId;
    private String invoiceType;
    private String invoiceNumber;
    private Double amount;
    private Double discountAmount;
    private String invoiceDueDate;
    private String invoiceGeneratedDate;
    private String invoiceStartDate;
    private String invoiceEndDate;
    private String paidAt;
    private String paymentDate;
    private Double paidAmount;
    private Double balanceAmount;
    private Double dueAmount;
    private String status;
    private Boolean isCancelled;
    private boolean canShowPaymentDate;
    private String adjustmentStatus;
    private String lastAdjustedDate;
    private String lastAdjustedTime;
}

