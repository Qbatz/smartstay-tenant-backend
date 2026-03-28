package com.smartstay.tenant.dto.invoice;

import com.smartstay.tenant.response.eb.InvoiceEbResponse;
import com.smartstay.tenant.response.invoices.UnpaidInvoices;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDetailsDTO {

    private String invoiceId;
    private String invoiceNumber;
    private String invoiceType;

    private String generatedDate;

    private String dueDate;

    private String startDate;

    private String endDate;

    private Double totalAmount;
    private Double discountAmount;
    private Double paidAmount;
    private Double dueAmount;
    private String status;

    private Double gst;
    private Double cgst;
    private Double sgst;
    private Double gstPercentile;

    private List<InvoiceItemDTO> invoiceItems;
    private List<ReceiptDTO> receipts;

    private List<UnpaidInvoices> unpaidInvoices;
    private InvoiceEbResponse ebInfo;

    private String lastPaidDate;
    private String lastPaymentMode;
    private String lastReferenceId;
    private Boolean showMessage;
}
