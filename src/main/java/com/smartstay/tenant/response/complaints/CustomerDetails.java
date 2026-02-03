package com.smartstay.tenant.response.complaints;

public record CustomerDetails(String customerId,
                              String fullName,
                              String firstName,
                              String lastName,
                              String profilePic,
                              String initials,
                              String countryCode,
                              String mobile) {
}
