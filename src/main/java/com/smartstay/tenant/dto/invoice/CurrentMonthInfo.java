package com.smartstay.tenant.dto.invoice;

import java.util.List;

//invoice start date to final settlement date---no of days
//Start date bed history take rent from bed history---and the get the billing rule strat date and enddate
// greater than current cycle rent and reassign rent
public record CurrentMonthInfo(long noOfDaysStayed,
                               Double payableRent,
                               Double lastRentPaid,
                               List<BedHistory> bedHistories) {
}

//billing cycle 1 to 30
//
//this month 1 to 31
//
//5 date join 6 th shift 7 shift
//10 th shift 11 th shift
