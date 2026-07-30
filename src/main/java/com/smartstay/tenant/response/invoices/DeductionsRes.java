package com.smartstay.tenant.response.invoices;

public record DeductionsRes(String type,
                            Double amount,
                            Double paidAmount) {
}
