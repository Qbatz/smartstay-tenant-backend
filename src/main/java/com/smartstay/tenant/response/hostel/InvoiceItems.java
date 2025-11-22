package com.smartstay.tenant.response.hostel;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class InvoiceItems {
    private String invoiceId;
    private String invoiceNumber;
    private String invoiceType;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date generatedDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dueDate;

    private String itemType;
    private Double totalAmount;
    private Double totalPaid;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date lastPaidDate;
}
