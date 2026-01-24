package com.smartstay.tenant.response.complaints;

import java.util.List;

public record ComplaintDetails(Integer complaintId,
                               String complaintDescription,
                               Integer complaintTypeId,
                               String complaintType,
                               String currentStatus,
                               String raisedAt,
                               String time,
                               CustomerDetails customerDetails,
                               UserDetails assignee,
                               List<ComplaintImages> complaintImages) {
}
