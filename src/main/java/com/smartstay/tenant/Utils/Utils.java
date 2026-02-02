package com.smartstay.tenant.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public final class Utils {

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
    public static final String NOTIFICATION_NOT_FOUND = "Notifications not found.";
    public static final String COMPLAINT_IMAGE_NOT_FOUND = "Complaint image not found.";
    public static final String HOSTEL_NOT_FOUND = "Hostel not found.";
    public static final String REQUEST_ALREADY_EXISTS = "Request already exists.";
    public static final String PENDING_REQUEST_EXISTS = "Request already exists.";
    public static final String REQUEST_SENT_SUCCESSFULLY = "Request sent successfully.";
    public static final String COMPLAINT_NOT_FOUND = "Complaint not found.";
    public static final String NO_RECORDS_FOUND = "No records found";
    public static final String INVALID_REQUEST_TYPE = "Invalid request type.";
    public static final String CREATED = "Created Successfully";
    public static final String DELETED = "Deleted Successfully";
    public static final String INVALID_HOSTEL_ID = "Invalid hostel id";
    public static final String INVALID_TRANSACTION_ID = "Invalid transaction id";
    public static final String INVALID_INVOICE_ID = "Invalid invoice id";
    public static final String INVALID_INVOICE_DATE = "Invalid invoice date";
    public static final String PAYLOADS_REQUIRED = "Payloads required";
    public static final String UPDATED = "Updated Successfully";
    public static final String INVOICE_ITEMS_NOT_FOUND = "Invoice items not found.";
    public static final String INVOICE_NOT_FOUND = "Invoice not found.";
    public static final String PAYMENTS_NOT_FOUND = "Payments not found.";
    public static final String COMPLAINT_TYPE_NOT_FOUND = "Complaint type not found.";
    public static final String INVALID_COMPLAINT_ID = "Invalid complaint id passed";
    public static final String INVALID_REQUEST = "Invalid request";
    public static final String ENVIRONMENT_LOCAL = "LOCAL";
    public static final String ENVIRONMENT_DEV = "DEV";
    public static final String ENVIRONMENT_QA = "QA";
    public static final String ENVIRONMENT_PROD = "PROD";

    public static final String CANNOT_MAKE_BED_CHANGE_REQUEST_NOTICE_CUSTOMER = "Bed change is not allowed during the notice period.";
    private Utils() {
    }

    public static Date findLastDate(Integer cycleStartDay, Date date) {
        LocalDate today = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
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

    public static String dateToTime(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(OUTPUT_TIME_FORMAT).format(date);
    }


    public static Date convertStringToDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date addDaysToDate(Date date, int noOfDays) {
        return Date.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(noOfDays).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static String capitalize(String value) {
        if (value == null || value.isEmpty()) return value;
        return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
    }

    public static String dateToDateMonth(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(OUTPUT_DATE_MONTH_FORMAT).format(date);
    }

    public static int compareWithTwoDates(Date date1, Date date2) {
        LocalDate localDate1 = date1.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate localDate2 = date2.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return localDate1.compareTo(localDate2);
    }

    public static String dateToMonth(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(OUTPUT_MONTH_FORMAT).format(date);
    }

    public static String getInitials(String name) {

        if (name == null || name.trim().isEmpty()) {
            return "";
        }

        String[] parts = name.trim().split("\\s+");

        if (parts.length == 1) {
            return parts[0].length() >= 2 ? parts[0].substring(0, 2).toUpperCase() : parts[0].substring(0, 1).toUpperCase();
        }

        char first = parts[0].charAt(0);
        char last = parts[parts.length - 1].charAt(0);

        return ("" + first + last).toUpperCase();
    }

    public static String formatComplaintDate(Date date) {
        if (date == null) return "";

        long now = System.currentTimeMillis();
        long diffMillis = now - date.getTime();

        long minutes = diffMillis / (60 * 1000);
        long hours = diffMillis / (60 * 60 * 1000);

        if (hours < 24) {
            if (hours >= 1) {
                return hours + " hrs ago";
            }
            if (minutes >= 1) {
                return minutes + " mins ago";
            }
            return "Just now";
        }

        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    public static long findNumberOfDays(Date date1, Date date2) {
        LocalDate start = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end   = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return ChronoUnit.DAYS.between(start, end) + 1;
    }

    public static long findNoOfDaysInCurrentMonth(Date date) {
        LocalDate localDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return localDate.lengthOfMonth();
    }
    public static Double roundOffWithTwoDigit(double number) {
        return Math.round(number * 100.0) / 100.0;
    }


}
