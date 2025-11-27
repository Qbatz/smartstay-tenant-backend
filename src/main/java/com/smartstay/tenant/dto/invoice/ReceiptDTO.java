package com.smartstay.tenant.dto.invoice;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptDTO {

    private String transactionId;
    private String referenceNumber;
    private String paymentMode;
    private String bankName;
    private Double paidAmount;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date paidAt;
}
