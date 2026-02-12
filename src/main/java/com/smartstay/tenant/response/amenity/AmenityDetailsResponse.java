package com.smartstay.tenant.response.amenity;

public record AmenityDetailsResponse(String amenityId,
                                     String amenityName,
                                     Double amenityAmount,
                                     String description,
                                     String termsAndCondition,
                                     Boolean proRate,
                                     Boolean isAssigned,
                                     String currentBillStartDate,
                                     String currentBillEndDate,
                                     String currentBillDueDate,
                                     String nextBillStartDate,
                                     String nextBillEndDate,
                                     String nextBillDueDate) {
}
