package com.smartstay.tenant.response.complaints;

import java.util.List;

public record ComplaintsUpdates(String complaintId, List<ComplaintUpdatesList> complaintsUpdates) {
}
