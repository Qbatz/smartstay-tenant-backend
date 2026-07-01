package com.smartstay.tenant.response.invoiceRedemption;

public record InvRedemptionRes(Long redemptionId,
                               String invoiceId,
                               String invoiceNumber,
                               double redemptionAmount,
                               String redeemedAtDate,
                               String redeemedAtTime) {
}
