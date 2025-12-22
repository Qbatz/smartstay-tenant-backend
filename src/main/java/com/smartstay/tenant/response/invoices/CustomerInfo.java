package com.smartstay.tenant.response.invoices;

public record CustomerInfo(String firstName, String lastName, String fullName, String customerId,
                           String customerMobileNo, String countryCode, String fullAddress,

                           String houseNo, String street, String city, String state, int pinCode,
                           String joiningDate) {
}
