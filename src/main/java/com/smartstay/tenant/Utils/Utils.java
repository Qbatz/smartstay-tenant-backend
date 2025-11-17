package com.smartstay.tenant.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public final class Utils {

    private Utils() {}

    public static final String USER_INPUT_DATE_FORMAT = "dd-MM-yyyy";

    public static final String OUTPUT_DATE_FORMAT = "dd/MM/yyyy";
    public static final String OUTPUT_TIME_FORMAT = "hh:mm:ss aa";
    public static final String OUTPUT_MONTH_FORMAT = "MMM YYYY";
    public static final String OUTPUT_DATE_MONTH_FORMAT = "dd MMM";

    public static final String UNAUTHORIZED = "Unauthorized";
    public static final String SUCCESS = "Success";
    public static final String FAILED = "Failed";

    public static final String CUSTOMER_NOT_FOUND = "Customer not found.";
    public static final String COMPLAINTS_NOT_FOUND = "Complaints not found.";
    public static final String COMPLAINT_IMAGE_NOT_FOUND = "Complaint image not found.";
    public static final String HOSTEL_NOT_FOUND = "Hostel not found.";
    public static final String COMPLAINT_NOT_FOUND = "Complaint not found.";
    public static final String NO_RECORDS_FOUND = "No records found";

    public static final String CREATED = "Created Successfully";
    public static final String DELETED = "Deleted Successfully";

    public static final String PAYLOADS_REQUIRED = "Payloads required";
    public static final String UPDATED = "Updated Successfully";

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

    public static Date stringToDate(String date, String inputFormat) {
        try {
            return new SimpleDateFormat(inputFormat).parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format");
        }
    }

    public static String dateToString(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(OUTPUT_DATE_FORMAT).format(date);
    }



}
