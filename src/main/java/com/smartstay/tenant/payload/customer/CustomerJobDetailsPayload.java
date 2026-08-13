package com.smartstay.tenant.payload.customer;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record CustomerJobDetailsPayload(Long jobId,
                                        String employmentStatus,
                                        String organizationName,
                                        String role,
                                        String workLocation,
                                        @JsonFormat(pattern = "dd-MM-yyyy")
                                        LocalDate workStartDate,
                                        @JsonFormat(pattern = "dd-MM-yyyy")
                                        LocalDate workEndDate,
                                        String shiftType,
                                        String shiftFrom,
                                        String shiftTo) {
}
