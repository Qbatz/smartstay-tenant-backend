package com.smartstay.tenant.Utils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public final class Utils {

    private Utils() {}

    public static final String UNAUTHORIZED = "Unauthorized";
    public static final String SUCCESS = "Success";
    public static final String FAILED = "Failed";

    public static final String CUSTOMER_NOT_FOUND = "Customer not found.";


    public static final Date findLastDate(Integer cycleStartDay, Date date) {
        LocalDate today = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();;
        LocalDate startDate = LocalDate.of(today.getYear(), today.getMonth(), cycleStartDay);

        LocalDate cycleEnd;
        if (cycleStartDay == 1) {
            YearMonth ym = YearMonth.from(startDate);
            cycleEnd = ym.atEndOfMonth();
        } else {
            LocalDate nextMonth = startDate.plusMonths(1);
            int endDay = cycleStartDay - 1;

            int lastDayOfNextMonth = YearMonth.from(nextMonth).lengthOfMonth();
            if (endDay > lastDayOfNextMonth) {
                endDay = lastDayOfNextMonth;
            }

            cycleEnd = nextMonth.withDayOfMonth(endDay);
        }

        return Date.from(cycleEnd.atStartOfDay(ZoneId.systemDefault()).toInstant());

    }


    public static Date getFirstDayOfPreviousMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }



}
