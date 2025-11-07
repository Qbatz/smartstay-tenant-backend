package com.smartstay.tenant.response.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDetails {
    private String customerId;
    private String firstName;
    private String lastName;
    private String houseNo;
    private String street;
    private String landmark;
    private int pincode;
    private String city;
    private String state;
    private String profilePic;
}
