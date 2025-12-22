package com.smartstay.tenant.dto.complaint;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record ComplaintResponse(
        Integer complaintId,
        String complaintTypeName,

        Integer complaintTypeId,
        String assigneeMobileNumber,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Asia/Kolkata")
        Date complaintDate,
        String description,
        String status,

        String assigneeName,

        String complaintDateDisplay
) {
}