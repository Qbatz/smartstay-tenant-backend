package com.smartstay.tenant.response.customer;

import com.smartstay.tenant.response.PaginationDataRes;
import com.smartstay.tenant.response.hostel.RequestItemResponse;

import java.util.List;

public record CustomerRequestsResWrapper(PaginationDataRes paginationData,
                                         List<RequestItemResponse> customerRequests) {
}
