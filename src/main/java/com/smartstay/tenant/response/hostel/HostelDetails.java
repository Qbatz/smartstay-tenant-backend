package com.smartstay.tenant.response.hostel;

import com.smartstay.tenant.dto.complaint.ComplaintDateResponse;

import java.util.List;

public record HostelDetails(InvoiceSummary previousMonthBills, InvoiceSummary currentMonthBills,
                            List<ComplaintDateResponse> complaints) {
}
