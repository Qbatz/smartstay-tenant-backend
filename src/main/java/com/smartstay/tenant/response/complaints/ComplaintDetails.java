package com.smartstay.tenant.response.complaints;

import com.smartstay.tenant.dto.complaint.ComplaintComments;

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
                               List<ComplaintImages> complaintImages,
                               List<ComplaintComments> comments) {
}
