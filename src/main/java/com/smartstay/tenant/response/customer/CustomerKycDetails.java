package com.smartstay.tenant.response.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerKycDetails {

    private String currentStatus;
    private String transactionId;
    private String referenceId;
}
