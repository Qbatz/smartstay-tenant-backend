package com.smartstay.tenant.response.hostel;

import com.smartstay.tenant.dto.ComplaintDTO;

import java.util.List;

public record HostelDetails(InvoiceSummary previousMonthBills, InvoiceSummary currentMonthBills,
                            List<ComplaintDTO> complaints) {
}
