package com.smartstay.tenant.response.hostel;

public record Complaints(
        String complaintId,
        String title,
        String description,
        String status
) {
}
