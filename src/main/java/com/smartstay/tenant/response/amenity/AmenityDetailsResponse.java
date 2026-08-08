package com.smartstay.tenant.response.amenity;

public record AmenityDetailsResponse(String amenityId,
                                     String amenityName,
                                     Double amenityAmount,
                                     String description,
                                     String termsAndCondition,
                                     Boolean proRate,
                                     Boolean isAssigned,
                                     Boolean isRequestRaised,
                                     Long requestId,
                                     String requestDate,
                                     String requestTime,
                                     String currentBillStartDate,
                                     String currentBillEndDate,
                                     String currentBillDueDate,
                                     String nextBillStartDate,
                                     String nextBillEndDate,
                                     String nextBillDueDate) {
}
