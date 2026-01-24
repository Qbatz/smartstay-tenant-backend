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
public class FinalSettlementDetails {
    private String invoiceId;
    private String invoiceNumber;
    private String invoiceType;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date generatedDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dueDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date startDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date endDate;

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

    private AdvanceInfo advanceInfo;
    private CurrentMonthInfo currentMonthInfo;


    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date lastPaidDate;
    private String lastPaymentMode;
    private String lastReferenceId;
    private Boolean showMessage;
}
