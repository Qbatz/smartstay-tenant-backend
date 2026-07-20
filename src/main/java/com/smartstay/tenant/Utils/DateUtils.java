package com.smartstay.tenant.Utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {

    public static String getDuration(Date joiningDate, Date checkoutDate) {

        if (joiningDate == null || checkoutDate == null) {
            return null;
        }

        LocalDate start = joiningDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalDate end = checkoutDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        if (end.isBefore(start)) {
            return null;
        }

        Period period = Period.between(start, end);

        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();

        // Minimum stay is 1 day
        if (years == 0 && months == 0 && days == 0) {
            return "1 day";
        }

        StringBuilder duration = new StringBuilder();

        if (years > 0) {
            duration.append(years)
                    .append(years == 1 ? " year" : " years");
        }

        if (months > 0) {
            if (!duration.isEmpty()) {
                duration.append(" ");
            }
            duration.append(months)
                    .append(months == 1 ? " month" : " months");
        }

        // Only show days when there are no months or years
        if (years == 0 && months == 0) {
            duration.append(days == 1 ? "1 day" : days + " days");
        }

        return duration.toString();
    }
}
