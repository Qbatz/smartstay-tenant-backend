package com.smartstay.tenant.dto.invoice;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
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
    private LocalDate invoiceDueDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate invoiceGeneratedDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate invoiceStartDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate paidAt;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate paymentDate;
    private Double paidAmount;
    private Double dueAmount;
    private String status;
}

