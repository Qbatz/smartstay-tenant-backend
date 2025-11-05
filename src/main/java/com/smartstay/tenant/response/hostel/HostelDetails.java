package com.smartstay.tenant.response.hostel;

public record HostelDetails(
        String hostelId,
        String hostelName,
        String houseNo,
        String street,
        String landmark,
        int pincode,
        String city,
        String state
) {
}
