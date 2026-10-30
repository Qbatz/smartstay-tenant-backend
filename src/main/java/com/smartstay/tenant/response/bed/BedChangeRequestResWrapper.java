package com.smartstay.tenant.response.bed;

import com.smartstay.tenant.response.PaginationDataRes;
import com.smartstay.tenant.response.hostel.RequestItemResponse;

import java.util.List;

public record BedChangeRequestResWrapper(PaginationDataRes paginationData,
                                         List<RequestItemResponse> bedChangeRequests) {
}
