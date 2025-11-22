package com.smartstay.tenant.service;


import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.ennum.RequestType;
import com.smartstay.tenant.payload.notification.NotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class BedsService {

    @Autowired
    private Authentication authentication;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private NotificationService notificationService;

    public ResponseEntity<?> requestBedChange(String hostelId, NotificationRequest request) {
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
        notificationService.createNotificationForBedChange(customerId, hostelId, request);
        return new ResponseEntity<>(Utils.REQUEST_SENT_SUCCESSFULLY, HttpStatus.OK);
    }
}
