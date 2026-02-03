package com.smartstay.tenant.response.complaints;

public record UserDetails(String userId,
                          String firstName,
                          String lastName,
                          String fullName,
                          String initials,
                          String profilePic,
                          String mobile,
                          String countryCode) {
}
