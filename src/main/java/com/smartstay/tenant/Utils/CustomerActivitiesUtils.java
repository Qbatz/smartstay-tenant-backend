package com.smartstay.tenant.Utils;

import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.ennum.CustomerActivitySourceEnum;
import com.smartstay.tenant.ennum.CustomerActivityTypeEnum;

public class CustomerActivitiesUtils {

    public static String buildDescription(Customers loggedInCustomer,
                                          CustomerActivityTypeEnum customerActivityType,
                                          CustomerActivitySourceEnum customerActivitySource) {

        if (customerActivityType == null){
            return null;
        }

        return switch (customerActivityType) {
            case LOGIN -> buildLoginDescription(customerActivitySource, loggedInCustomer);
            case CREATE -> buildCreateDescription(customerActivitySource, loggedInCustomer);
            case UPDATE -> buildUpdateDescription(customerActivitySource, loggedInCustomer);
            case DELETE -> buildDeleteDescription(customerActivitySource, loggedInCustomer);
        };
    }

    private static String buildLoginDescription(CustomerActivitySourceEnum customerActivitySource,
                                                Customers loggedInCustomer) {

        if (customerActivitySource == null || loggedInCustomer == null){
            return "Logged in";
        }

        String customerName = Utils
                .getFullName(loggedInCustomer.getFirstName(), loggedInCustomer.getLastName());

        return switch (customerActivitySource) {
            case LOGGED_IN -> customerName + " has logged in successfully";
            default -> "Logged-in successfully";
        };
    }

    private static String buildCreateDescription(CustomerActivitySourceEnum customerActivitySource,
                                                 Customers loggedInCustomer) {

        if (customerActivitySource == null || loggedInCustomer == null){
            return "Created";
        }

        String customerName = Utils
                .getFullName(loggedInCustomer.getFirstName(), loggedInCustomer.getLastName());

        return switch (customerActivitySource) {
            case AMENITY_REQUEST -> customerName + " has created an amenity request";
            case BED_CHANGE_REQUEST -> customerName + " has created an bed change request";
            case COMPLAINT -> customerName + " has created a complaint";
            case COMPLAINT_COMMENTS -> customerName + " has added a comment to a complaint";
            case CUSTOMER_ADDITIONAL_CONTACTS -> customerName + " has added an additional contact";
            case CUSTOMER_OTP -> customerName + " has requested an otp";
            case RAISE_NOTICE_REQUEST -> customerName + " has raised a notice request";
            case CUSTOMER_DOCUMENTS -> customerName + " has added a document";
            case CUSTOMER_JOB_DETAILS -> customerName + " has added a job detail";
            case KYC_DETAILS -> customerName + " has initiated kyc verification";
            default -> "Created successfully";
        };
    }

    private static String buildUpdateDescription(CustomerActivitySourceEnum customerActivitySource,
                                                 Customers loggedInCustomer) {

        if (customerActivitySource == null || loggedInCustomer == null){
            return "Updated";
        }

        String customerName = Utils
                .getFullName(loggedInCustomer.getFirstName(), loggedInCustomer.getLastName());

        return switch (customerActivitySource) {
            case COMPLAINT -> customerName + " has updated a complaint";
            case CUSTOMER_CREDENTIALS -> customerName + " has updated customer credential";
            case CUSTOMER_ADDITIONAL_CONTACTS -> customerName + " has updated an additional contact";
            case CUSTOMER -> customerName + " has updated profile";
            case CUSTOMER_OTP -> customerName + " has re-requested an otp";
            case CUSTOMER_JOB_DETAILS -> customerName + " has updated a job detail";
            case KYC_DETAILS -> customerName + " has updated kyc details";
            default -> "Updated successfully";
        };
    }

    private static String buildDeleteDescription(CustomerActivitySourceEnum customerActivitySource,
                                                 Customers loggedInCustomer) {

        if (customerActivitySource == null || loggedInCustomer == null){
            return "Deleted";
        }

        String customerName = Utils
                .getFullName(loggedInCustomer.getFirstName(), loggedInCustomer.getLastName());

        return switch (customerActivitySource) {
            case AMENITY_REQUEST -> customerName + " has deleted an amenity request";
            case COMPLAINT -> customerName + " has deleted a complaint";
            case COMPLAINT_IMAGES -> customerName + " has deleted an image of a complaint";
            case BED_CHANGE_REQUEST -> customerName + " has deleted a bed change request";
            default -> "Deleted successfully";
        };
    }
}
