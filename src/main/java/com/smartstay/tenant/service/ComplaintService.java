package com.smartstay.tenant.service;

import com.smartstay.tenant.dto.ComplaintDTO;
import com.smartstay.tenant.repository.ComplaintsV1Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintsV1Repository complaintsV1Repository;

    public ComplaintService(ComplaintsV1Repository complaintsV1Repository) {
        this.complaintsV1Repository = complaintsV1Repository;
    }

    public List<ComplaintDTO> getComplaints(String hostelId, String customerId) {
        return complaintsV1Repository.findComplaintsByHostelAndCustomer(hostelId, customerId);
    }
}
