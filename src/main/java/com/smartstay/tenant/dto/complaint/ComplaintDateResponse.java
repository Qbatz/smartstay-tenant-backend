package com.smartstay.tenant.dto.complaint;

public record ComplaintDateResponse(
        Integer complaintId,
        String complaintTypeName,

        Integer complaintTypeId,
        String assigneeMobileNumber,

        String complaintDate,
        String complaintTime,
        String complaintDateDisplay,

        String description,
        String status,

        String assigneeName
) {


}
