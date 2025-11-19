package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.repository.ComplaintTypeV1Repository;
import com.smartstay.tenant.response.complaints.ComplaintTypeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ComplaintTypeService {


    @Autowired
    ComplaintTypeV1Repository complaintTypeV1Repository;

    @Autowired
    private Authentication authentication;

    @Autowired
    private CustomerService customerService;


    public ResponseEntity<?> getAllComplaintTypes(String hostelId) {
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        String customerId = authentication.getName();
        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        List<ComplaintTypeResponse> complaintTypeResponses = complaintTypeV1Repository.getAllComplaintsType(hostelId);
        if (complaintTypeResponses.isEmpty()) {
            return new ResponseEntity<>(Utils.NO_RECORDS_FOUND, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(complaintTypeResponses, HttpStatus.OK);
    }
}
