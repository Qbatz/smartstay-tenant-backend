package com.smartstay.tenant.dto.bills;

public record BillTemplates(String prefix,
                            String suffix,
                            Double gstPercentile) {
}
