package com.smartstay.tenant.response.complaints;

import com.smartstay.tenant.dto.complaint.ComplaintResponse;
import com.smartstay.tenant.response.PaginationDataRes;

import java.util.List;

public record ComplaintResWrapper(PaginationDataRes paginationData,
                                  List<ComplaintResponse> complaints) {
}
