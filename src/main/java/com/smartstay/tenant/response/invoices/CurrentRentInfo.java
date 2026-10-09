package com.smartstay.tenant.response.invoices;

import java.util.List;

public record CurrentRentInfo(String labelText,
                              double currentMonthPaidAmount,
                              double currentMonthPayableAmount,
                              double currentMonthPendingAmount,
                              double currentMonthStayDays,
                              double currentMonthOtherItemAmount,
                              List<RentBreakUp> listBreakup,
                              List<CurrentMonthOtherItemsRes> listCurrentMonthOtherItems) {
}
