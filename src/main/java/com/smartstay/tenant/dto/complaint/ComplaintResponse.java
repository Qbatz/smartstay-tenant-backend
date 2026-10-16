package com.smartstay.tenant.dto.complaint;

public record ComplaintResponse(
        Integer complaintId,
        String complaintTypeName,

        Integer complaintTypeId,
        String assigneeMobileNumber,
        String complaintDate,
        String description,
        String status,

        String assigneeName,

        String complaintDateDisplay
) {
}