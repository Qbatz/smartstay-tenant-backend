package com.smartstay.tenant.response.customer;

import jakarta.validation.constraints.Email;

public record EditCustomer(
        String firstName,
        String lastName,
        @Email
        String emailId,
        String mobile,
        String houseNo,
        String street,
        String landmark,
        String city,
        String state,
        String dob,
        String gender
) {
}
