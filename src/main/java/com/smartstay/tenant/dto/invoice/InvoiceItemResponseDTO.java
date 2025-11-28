package com.smartstay.tenant.dto.invoice;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceItemResponseDTO {

    private String invoiceId;
    private String invoiceType;
    private String invoiceNumber;
    private Double amount;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date invoiceDueDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date invoiceGeneratedDate;
    private Double paidAmount;
    private Double dueAmount;
    private String status;
}

