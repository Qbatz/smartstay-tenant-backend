package com.smartstay.tenant.response.customer;

import com.smartstay.tenant.payload.customer.CustomerAdditionalContactsEditPayload;
import jakarta.validation.constraints.Email;

import java.util.List;

public record EditCustomer(
        String firstName,
        String lastName,
        @Email
        String emailId,
        String houseNo,
        String street,
        String landmark,
        String city,
        String state,
        String dob,
        String gender,
        List<CustomerAdditionalContactsEditPayload> additionalContacts
) {
}
