package com.smartstay.tenant.service;


import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.BedChangeRequest;
import com.smartstay.tenant.ennum.RequestStatus;
import com.smartstay.tenant.ennum.RequestType;
import com.smartstay.tenant.payload.bedChange.BedChangePayload;
import com.smartstay.tenant.payload.notification.NotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class BedsService {

    @Autowired
    private Authentication authentication;

    @Autowired
    private BedChangeRequestService bedChangeRequestService;

    @Autowired
    private CustomerService customerService;

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
        boolean exists = notificationService.checkRequestExists(customerId, hostelId, RequestType.CHANGE_BED);
        if (exists) {
            return new ResponseEntity<>(Utils.REQUEST_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }
        boolean requested = bedChangeRequestService.existsPendingRequest(customerId, hostelId);
        if (requested) {
            return new ResponseEntity<>(Utils.PENDING_REQUEST_EXISTS, HttpStatus.BAD_REQUEST);
        }
        bedChangeRequestService.saveBedChangeRequest(hostelId, customerId, request);
        notificationService.createNotificationForBedChange(customerId, hostelId, request);
        return new ResponseEntity<>(Utils.REQUEST_SENT_SUCCESSFULLY, HttpStatus.OK);
    }
}
