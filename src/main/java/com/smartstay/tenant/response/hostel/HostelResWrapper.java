package com.smartstay.tenant.response.hostel;

import com.smartstay.tenant.response.PaginationDataRes;
import com.smartstay.tenant.response.customer.CustomerHostels;

import java.util.List;

public record HostelResWrapper(PaginationDataRes paginationData,
                               List<CustomerHostels> hostels) {
}
