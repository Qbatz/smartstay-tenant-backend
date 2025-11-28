package com.smartstay.tenant.dto.invoice;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDetailsDTO {

    private String invoiceId;
    private String invoiceNumber;
    private String invoiceType;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date generatedDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dueDate;

    private Double totalAmount;
    private Double paidAmount;
    private Double dueAmount;
    private String status;

    private Double gst;
    private Double cgst;
    private Double sgst;
    private Double gstPercentile;

    private List<InvoiceItemDTO> invoiceItems;
    private List<ReceiptDTO> receipts;
}
