package com.smartstay.tenant.dto.invoice;

import java.util.List;

public record CurrentMonthInfo(long noOfDaysStayed,
                               Double payableRent,
                               Double lastRentPaid,
                               List<BedHistory> bedHistories) {
}
