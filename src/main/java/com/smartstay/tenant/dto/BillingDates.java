package com.smartstay.tenant.dto;

import java.util.Date;

public record BillingDates(Date currentBillStartDate,
                           Date currentBillEndDate,
                           Date dueDate,
                           Integer dueDays) {
}
