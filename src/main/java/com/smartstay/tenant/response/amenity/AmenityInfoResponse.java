package com.smartstay.tenant.response.amenity;

public record AmenityInfoResponse(String amenityId,
                                  boolean proRate,
                                  double amenityAmount,
                                  String amenityName,
                                  String amenityStartDate,
                                  String amenityEndDate) {
}
