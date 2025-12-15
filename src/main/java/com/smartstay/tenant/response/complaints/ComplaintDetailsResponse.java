package com.smartstay.tenant.response.complaints;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartstay.tenant.dto.comment.ComplaintCommentProjection;

import java.util.Date;
import java.util.List;

public record ComplaintDetailsResponse(Integer complaintId, String complaintTypeName,

                                       Integer complaintTypeId,
                                       @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Asia/Kolkata") Date complaintDate,

                                       String description, String status, String assigneeName, String floorName,
                                       String roomName, String bedName, String customerName,

                                       @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Asia/Kolkata")
                                       Date assignedDate,

                                       String createdBy, String hostelName, String assigneeMobileNumber,
                                       List<ComplaintImage> images, List<ComplaintCommentProjection> comments) {
}
