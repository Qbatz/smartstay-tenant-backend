package com.smartstay.tenant.response.hostel;

import com.smartstay.tenant.dto.ComplaintDTO;

import java.util.List;

public record HostelDetails(List<InvoiceItems> previousMonthBills, List<InvoiceItems> currentMonthBills,
                            List<ComplaintDTO> complaints) {
}
