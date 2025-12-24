package com.smartstay.tenant.response.invoices;

public record AccountDetails(String accountNo,
                             String ifscCode,
                             String bankName,
                             String upiId,
                             String qrCode) {
}
