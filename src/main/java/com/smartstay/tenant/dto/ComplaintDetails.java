package com.smartstay.tenant.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartstay.tenant.response.complaints.ComplaintComment;
import com.smartstay.tenant.response.complaints.ComplaintImage;

import java.util.Date;
import java.util.List;

public record ComplaintDetails(
        Integer complaintId,
        String complaintTypeName,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Asia/Kolkata")
        Date complaintDate,

        String description,
        String status,
        String assigneeName,
        String floorName,
        String roomName,
        String bedName,
        String customerName,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Asia/Kolkata")
        Date assignedDate,

        String createdBy,
        String hostelName,
        String assigneeMobileNumber
) {}

