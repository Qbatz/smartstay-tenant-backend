package com.smartstay.tenant.dto;

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
    private String status;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date invoiceDueDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date invoiceGeneratedDate;

    private String itemType;
}

