package com.smartstay.tenant.response.customer;

public record EditCustomer(
        String firstName,
        String lastName,
        String houseNo,
        String street,
        String landmark,
        String city,
        String state
) {
}
