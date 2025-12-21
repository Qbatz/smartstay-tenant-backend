package com.smartstay.tenant.service;


import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.BedChangeRequest;
import com.smartstay.tenant.dto.BedChangeRequestResponse;
import com.smartstay.tenant.dto.BedDetails;
import com.smartstay.tenant.ennum.CustomerStatus;
import com.smartstay.tenant.ennum.RequestStatus;
import com.smartstay.tenant.ennum.RequestType;
import com.smartstay.tenant.payload.bedChange.BedChangePayload;
import com.smartstay.tenant.payload.notification.NotificationRequest;
import com.smartstay.tenant.repository.BedsRepository;
import com.smartstay.tenant.response.amenity.AmenityRequestResponse;
import com.smartstay.tenant.response.hostel.RequestItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class BedsService {

    @Autowired
    private Authentication authentication;

    @Autowired
    private BedChangeRequestService bedChangeRequestService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BedsRepository bedsRepository;

    @Autowired
    private NotificationService notificationService;

    public ResponseEntity<?> requestBedChange(String hostelId, BedChangePayload request) {
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        List<String> currentStatus = Arrays.asList(CustomerStatus.CHECK_IN.name(), CustomerStatus.NOTICE.name());
        boolean customerExist = customerService.existsByHostelIdAndCustomerIdAndStatusesIn(hostelId, customerId, currentStatus);
        if (!customerExist) {
            return new ResponseEntity<>(Utils.CUSTOMER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        boolean requested = bedChangeRequestService.existsPendingRequest(customerId, hostelId);
        if (requested) {
            return new ResponseEntity<>(Utils.PENDING_REQUEST_EXISTS, HttpStatus.BAD_REQUEST);
        }
        bedChangeRequestService.saveBedChangeRequest(hostelId, customerId, request);
        notificationService.createNotificationForBedChange(customerId, hostelId, request);
        return new ResponseEntity<>(Utils.REQUEST_SENT_SUCCESSFULLY, HttpStatus.OK);
    }

    public ResponseEntity<?> getBedRequests(String hostelId) {
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        String customerId = authentication.getName();
        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        List<String> currentStatus = Arrays.asList(CustomerStatus.CHECK_IN.name(), CustomerStatus.NOTICE.name());
        boolean customerExist = customerService.existsByHostelIdAndCustomerIdAndStatusesIn(hostelId, customerId, currentStatus);
        if (!customerExist) {
            return new ResponseEntity<>(Utils.CUSTOMER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        List<RequestItemResponse> requestResponses = bedChangeRequestService.getRequests(hostelId, customerId);
        return new ResponseEntity<>(requestResponses, HttpStatus.OK);
    }

    public BedDetails getBedDetails(Integer bedId) {
        return bedsRepository.findByBedId(bedId);
    }
}
