package com.smartstay.tenant.response.amenity;

import com.smartstay.tenant.response.PaginationDataRes;
import com.smartstay.tenant.response.hostel.RequestItemResponse;

import java.util.List;

public record AmenityRequestResWrapper(PaginationDataRes paginationData,
                                       List<RequestItemResponse> amenityRequests) {
}
