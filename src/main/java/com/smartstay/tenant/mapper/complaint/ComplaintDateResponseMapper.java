package com.smartstay.tenant.mapper.complaint;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dto.ComplaintDTO;
import com.smartstay.tenant.dto.complaint.ComplaintDateResponse;

import java.util.Date;
import java.util.function.Function;

public class ComplaintDateResponseMapper implements Function<ComplaintDTO, ComplaintDateResponse> {
    @Override
    public ComplaintDateResponse apply(ComplaintDTO complaintDTO) {
        if (complaintDTO == null) return null;

        Date complaintDate = complaintDTO.complaintDate();
        String complaintDateString = Utils.dateToString(complaintDate);
        String complaintTime = Utils.dateToTime(complaintDate);
        String complaintDateDisplay = Utils.formatComplaintDate(complaintDate);

        return new ComplaintDateResponse(
                complaintDTO.complaintId(),
                complaintDTO.complaintTypeName(),
                complaintDTO.complaintTypeId(),
                complaintDTO.assigneeMobileNumber(),
                complaintDateString,
                complaintTime,
                complaintDateDisplay,
                complaintDTO.description(),
                complaintDTO.status(),
                complaintDTO.assigneeName()
        );
    }
}
