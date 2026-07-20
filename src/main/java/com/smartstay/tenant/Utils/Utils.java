package com.smartstay.tenant.Utils;

import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.dao.HostelV1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public final class Utils {

    public static final String USER_INPUT_DATE_FORMAT = "dd-MM-yyyy";
    public static final String INPUT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String OUTPUT_DATE_FORMAT = "dd/MM/yyyy";
    public static final String OUTPUT_TIME_FORMAT = "hh:mm:ss aa";
    public static final String OUTPUT_MONTH_FORMAT = "MMM YYYY";
    public static final String OUTPUT_DATE_MONTH_FORMAT = "dd MMM";
    public static final String UNAUTHORIZED = "Unauthorized";
    public static final String SUCCESS = "Success";
    public static final String FAILED = "Failed";
    public static final String TRY_AGAIN = "Try again";
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
    public static final String INVALID_PINCODE = "Invalid pincode";
    public static final String ENVIRONMENT_LOCAL = "LOCAL";
    public static final String ENVIRONMENT_DEV = "DEV";
    public static final String ENVIRONMENT_QA = "QA";
    public static final String ENVIRONMENT_PROD = "PROD";

    public static final String SERVER_ERROR = "Server error";
    public static final String RESPONSE_BODY_NOT_FOUND = "Response body not found";
    public static final String STATUS_NOT_FOUND = "Status not found";

    public static final String CUSTOMER_VERIFIED_KYC = "Customer is already verified";
    public static final String KYC_DETAILS_NOT_FOUND = "Kyc details not found";
    public static final String KYC_STATUS_MUST_BE_REQUESTED = "Kyc status must be requested";
    public static final String KYC_STATUS_CAN_NOT_BE_PENDING = "Kyc status can not be pending";
    public static final String KYC_STATUS_CAN_NOT_BE_VERIFIED = "Kyc status can not be verified";
    public static final String KYC_VERIFICATION_ALREADY_REQUESTED = "Kyc verification is already requested";
    public static final String KYC_ALREADY_REQUESTED = "Kyc already requested";
    public static final String KYC_ALREADY_VERIFIED = "Kyc already verified";
    public static final String KYC_REQUEST_EXPIRED = "Kyc request expired";
    public static final String KYC_REQUEST_PENDING_OR_NOT_AVAILABLE = "Kyc request pending or not available";

    public static final String CANNOT_MAKE_BED_CHANGE_REQUEST_NOTICE_CUSTOMER = "Bed change is not allowed during the notice period.";
    public static final String NO_DOCUMENT_ID_PROVIDED = "No document ID provided";
    public static final String NO_CONTACT_ID_PROVIDED = "No contact ID provided";
    public static final String INVALID_GENDER_VALUE = "Invalid gender value";
    public static final String NAME_IS_REQUIRED = "Name is required";
    public static final String MOBILE_IS_REQUIRED = "Mobile is required";
    public static final String CONTACT_ID_REQUIRED = "Contact ID is required";
    public static final String CONTACT_ID_CANT_BE_ZERO_OR_LESS = "Contact ID can't be 0 or less";
    public static final String DOCUMENT_ID_REQUIRED = "Document ID is required";
    public static final String DOCUMENT_ID_CANT_BE_ZERO_OR_LESS = "Document ID can't be 0 or less";
    public static final String PROFILE_PICTURE_REMOVED = "Profile picture is removed";
    public static final String DOCUMENT_CAN_BE_UPLOADED_BY_CHECK_IN_CUSTOMER = "Documents can only be uploaded by checked in customer";
    public static final String DOCUMENT_CAN_BE_DELETED_BY_CHECK_IN_CUSTOMER = "Documents can only be deleted by checked in customer";
    public static final String CUSTOMER_NOT_CHECKED_IN = "Customer not checked in";

    private Utils() {
    }

    public static Date findLastDate(Integer cycleStartDay, Date date) {
        LocalDate today = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        //LocalDate startDate = LocalDate.of(today.getYear(), today.getMonth(), cycleStartDay);
        YearMonth currentMonth = YearMonth.of(today.getYear(), today.getMonth());
        int safeStartDay = Math.min(cycleStartDay, currentMonth.lengthOfMonth());
        LocalDate startDate = currentMonth.atDay(safeStartDay);

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

    public static Date stringDateToDate(String date) {

        if (date == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(INPUT_DATE_TIME_FORMAT);
        LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);

        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String getFullName(String firstName, String lastName){

        StringBuilder fullName = new StringBuilder();

        if (firstName != null) {
            firstName = firstName.trim();
            fullName.append(firstName);
        }

        if (lastName != null && !lastName.isBlank()) {
            lastName = lastName.trim();
            fullName.append(" ");
            fullName.append(lastName);
        }

        return fullName.toString();
    }

    public static String getInitials(String firstName, String lastName){

        StringBuilder initials = new StringBuilder();

        if (firstName != null) {
            firstName = firstName.trim();
            initials.append(firstName.toUpperCase().charAt(0));
        }

        if (lastName != null && !lastName.isBlank()) {
            lastName = lastName.trim();
            initials.append(lastName.toUpperCase().charAt(0));
        }
        else {
            if (firstName != null) {
                String[] nameArr = firstName.split(" ");
                if (nameArr.length > 1) {
                    initials.append(nameArr[nameArr.length - 1].toUpperCase().charAt(0));
                }
                else {
                    String lastPart = nameArr[nameArr.length - 1].toUpperCase();

                    if (lastPart.length() > 1) {
                        initials.append(lastPart.charAt(1));
                    }
                }
            }
        }

        return initials.toString();
    }

    public static String getInitials2(String name){

        StringBuilder initials = new StringBuilder();

        if (name != null) {
            String[] arrName = name.split(" ");
            if (arrName.length > 0) {
                initials.append(arrName[0].toUpperCase().charAt(0));
            }
            if (arrName.length > 1) {
                initials.append(arrName[arrName.length - 1].toUpperCase().charAt(0));
            }
            else {
                String lastPart = arrName[arrName.length - 1].toUpperCase();

                if (lastPart.length() > 1) {
                    initials.append(lastPart.charAt(1));
                }
            }
        }

        return initials.toString();
    }

    public static String buildFullAddress(HostelV1 hostelV1) {

        if (hostelV1 == null){
            return null;
        }

        StringBuilder fullAddress = new StringBuilder();

        if (hostelV1.getHouseNo() != null &&
                !hostelV1.getHouseNo().trim().equalsIgnoreCase("")) {
            fullAddress.append(hostelV1.getHouseNo());
        }
        if (hostelV1.getStreet() != null) {
            if (fullAddress.isEmpty()) {
                fullAddress.append(hostelV1.getStreet());
            }
            else {
                fullAddress.append(", ");
                fullAddress.append(hostelV1.getStreet());
            }
        }
        if (hostelV1.getCity() != null) {
            if (fullAddress.isEmpty()) {
                fullAddress.append(hostelV1.getCity());
            }
            else {
                fullAddress.append(", ");
                fullAddress.append(hostelV1.getCity());
            }
        }
        if (hostelV1.getState() != null) {
            if (fullAddress.isEmpty()) {
                fullAddress.append(hostelV1.getState());
            }
            else {
                fullAddress.append(", ");
                fullAddress.append(hostelV1.getState());
            }
        }

        return fullAddress.toString();
    }

    public static String buildFullAddress(Customers customer) {

        if (customer == null){
            return null;
        }

        StringBuilder fullAddress = new StringBuilder();

        if (customer.getHouseNo() != null &&
                !customer.getHouseNo().trim().equalsIgnoreCase("")) {
            fullAddress.append(customer.getHouseNo());
        }
        if (customer.getStreet() != null) {
            if (fullAddress.isEmpty()) {
                fullAddress.append(customer.getStreet());
            }
            else {
                fullAddress.append(", ");
                fullAddress.append(customer.getStreet());
            }
        }
        if (customer.getCity() != null) {
            if (fullAddress.isEmpty()) {
                fullAddress.append(customer.getCity());
            }
            else {
                fullAddress.append(", ");
                fullAddress.append(customer.getCity());
            }
        }
        if (customer.getState() != null) {
            if (fullAddress.isEmpty()) {
                fullAddress.append(customer.getState());
            }
            else {
                fullAddress.append(", ");
                fullAddress.append(customer.getState());
            }
        }

        return fullAddress.toString();
    }
}
