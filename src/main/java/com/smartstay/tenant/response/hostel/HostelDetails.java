package com.smartstay.tenant.response.hostel;

import com.smartstay.tenant.dto.ComplaintDTO;
import com.smartstay.tenant.response.dashboard.InvoiceSummaryResponse;

import java.util.List;

public record HostelDetails(InvoiceSummaryResponse previousMonthBills, InvoiceSummaryResponse currentMonthBills,
                            List<ComplaintDTO> complaints) {
}
