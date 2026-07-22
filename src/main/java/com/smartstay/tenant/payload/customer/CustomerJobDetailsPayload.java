package com.smartstay.tenant.payload.customer;

public record CustomerJobDetailsPayload(Long jobId,
                                        String employmentStatus,
                                        String organizationName,
                                        String role,
                                        String workLocation,
                                        String shiftType,
                                        String shiftFrom,
                                        String shiftTo) {
}
