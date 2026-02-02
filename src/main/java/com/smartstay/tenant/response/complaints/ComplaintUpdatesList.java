package com.smartstay.tenant.response.complaints;

import com.smartstay.tenant.dto.complaint.ComplaintComments;

import java.util.List;

public record ComplaintUpdatesList(String update,
                                   String description,
                                   String updatedBy,
                                   String initials,
                                   String profilePic,
                                   String updatedAt,
                                   String updatedTime,
                                   String status,
                                   List<ComplaintComments> comments) {
}
