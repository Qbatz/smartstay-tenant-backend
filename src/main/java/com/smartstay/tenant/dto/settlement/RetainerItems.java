package com.smartstay.tenant.dto.settlement;

public record RetainerItems(String invoiceId,
                            String invoiceNo,
                            String invoiceDate,
                            Double totalAmount,
                            //available amount
                            // applied amount = totalAmount - amount
                            Double amount) {
}
