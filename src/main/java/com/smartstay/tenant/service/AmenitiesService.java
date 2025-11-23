package com.smartstay.tenant.service;


import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.payload.notification.NotificationRequest;
import com.smartstay.tenant.repository.AmentityRepository;
import com.smartstay.tenant.repository.CustomerAmenityRepository;
import com.smartstay.tenant.response.amenity.AmenityDetails;
import com.smartstay.tenant.response.amenity.AmenityInfoProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmenitiesService {
    @Autowired
    AmentityRepository amenityRepository;

    @Autowired
    NotificationService notificationService;
    @Autowired
    CustomerAmenityRepository customerAmenityRepository;
    @Autowired
    private Authentication authentication;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private UserHostelService userHostelService;

    public ResponseEntity<?> getAllAssignedAmenities(String hostelId) {
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        List<AmenityInfoProjection> amenitiesV1List = amenityRepository.findCurrentlyAssignedAmenities(hostelId, customerId);
        if (amenitiesV1List != null) {
            return new ResponseEntity<>(amenitiesV1List, HttpStatus.OK);
        }
        return new ResponseEntity<>(Utils.NO_RECORDS_FOUND, HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity<?> getAllUnAssignedAmenities(String hostelId) {
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        List<AmenityInfoProjection> amenitiesV1List = amenityRepository.findUnassignedAmenities(hostelId, customerId);
        if (amenitiesV1List != null) {
            return new ResponseEntity<>(amenitiesV1List, HttpStatus.OK);
        }
        return new ResponseEntity<>(Utils.NO_RECORDS_FOUND, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> getAmenityByAmenityId(String hostelId, String amenityId) {
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        AmenityDetails amenityInfo = amenityRepository.findAmenityByAmenityIdAndCustomerStatus(hostelId, amenityId, customerId);
        if (amenityInfo != null) {
            return new ResponseEntity<>(amenityInfo, HttpStatus.OK);
        }
        return new ResponseEntity<>(Utils.NO_RECORDS_FOUND, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> createAmenityRequest(String hostelId, NotificationRequest request) {
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        boolean exists = notificationService.checkRequestExists(customerId, hostelId, com.smartstay.tenant.ennum.RequestType.AMENITY_REQUEST);
        if (exists) {
            return new ResponseEntity<>(Utils.REQUEST_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }
        notificationService.createNotificationForAmenity(customerId, hostelId, request);
        return new ResponseEntity<>(Utils.REQUEST_SENT_SUCCESSFULLY, HttpStatus.OK);
    }

}
