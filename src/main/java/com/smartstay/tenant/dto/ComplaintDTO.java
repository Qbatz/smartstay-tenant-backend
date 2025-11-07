package com.smartstay.tenant.dto;

import java.util.Date;

public record ComplaintDTO(
        Integer complaintId,
        String complaintTypeName,
        Date complaintDate,
        String description,
        String status
) {
}