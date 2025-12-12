package com.smartstay.tenant.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record ComplaintDTO(
        Integer complaintId,
        String complaintTypeName,

        Integer complaintTypeId,
        String assigneeMobileNumber,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Asia/Kolkata")
        Date complaintDate,
        String description,
        String status,

        String assigneeName
) {
}