package com.smartstay.tenant.response.customer;

public record CustomerJobDetailsResponse(Long jobId,
                                         String employmentStatus,
                                         String organizationName,
                                         String role,
                                         String workLocation,
                                         String workStartDate,
                                         String workEndDate,
                                         String shiftType,
                                         String shiftFrom,
                                         String shiftTo) {
}
