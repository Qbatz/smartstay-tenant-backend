package com.smartstay.tenant.mapper.complaint;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dto.ComplaintDTO;
import com.smartstay.tenant.dto.complaint.ComplaintResponse;

import java.util.Date;
import java.util.function.Function;

public class ComplaintMapper implements Function<ComplaintDTO, ComplaintResponse> {

    @Override
    public ComplaintResponse apply(ComplaintDTO dto) {
        if (dto == null) return null;

        Date complaintDate = dto.complaintDate();
        String complaintDateDisplay = Utils.formatComplaintDate(complaintDate);
        return new ComplaintResponse(
                dto.complaintId(),
                dto.complaintTypeName(),
                dto.complaintTypeId(),
                dto.assigneeMobileNumber(),
                dto.complaintDate(),
                dto.description(),
                dto.status(),
                dto.assigneeName(),
                complaintDateDisplay
        );
    }
}
