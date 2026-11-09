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
            default -> "Deleted successfully";
        };
    }
}
