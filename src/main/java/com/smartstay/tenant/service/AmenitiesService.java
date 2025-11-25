package com.smartstay.tenant.service;


import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.payload.amenity.RequestAmenity;
import com.smartstay.tenant.repository.AmentityRepository;
import com.smartstay.tenant.repository.CustomerAmenityRepository;
import com.smartstay.tenant.response.amenity.AmenitiesStatusResponse;
import com.smartstay.tenant.response.amenity.AmenityDetails;
import com.smartstay.tenant.response.amenity.AmenityInfoProjection;
import com.smartstay.tenant.response.amenity.AmenityRequestResponse;
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
    AmenityRequestService amenityRequestService;

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

    public ResponseEntity<?> getAllAmenities(String hostelId) {
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        String customerId = authentication.getName();
        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        List<AmenityInfoProjection> assigned = amenityRepository.findCurrentlyAssignedAmenities(hostelId, customerId);
        List<AmenityInfoProjection> unassigned = amenityRepository.findUnassignedAmenities(hostelId, customerId);

        AmenitiesStatusResponse response = new AmenitiesStatusResponse(assigned != null ? assigned : List.of(), unassigned != null ? unassigned : List.of());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> getAmenityRequest(String hostelId) {
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        String customerId = authentication.getName();
        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        List<AmenityRequestResponse> requests = amenityRequestService.getRequests(customerId, hostelId);

        return new ResponseEntity<>(requests, HttpStatus.OK);
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

    public ResponseEntity<?> createAmenityRequest(String hostelId, String amenityId, RequestAmenity request) {
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        boolean exists = notificationService.checkRequestExists(customerId, hostelId, com.smartstay.tenant.ennum.RequestType.AMENITY_REQUEST,amenityId);
        if (exists) {
            return new ResponseEntity<>(Utils.REQUEST_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }
        if (amenityRequestService.existsPendingRequest(customerId, amenityId)) {
            return new ResponseEntity<>("Already requested. Please wait for approval.", HttpStatus.BAD_REQUEST);
        }

        Long count = amenityRepository.isAmenityAlreadyAssigned(customerId, amenityId);
        if (count != null && count > 0) {
            return new ResponseEntity<>("Amenity already assigned to this customer.", HttpStatus.BAD_REQUEST);
        }

        notificationService.createNotificationForAmenity(customerId, hostelId, request, amenityId);
        amenityRequestService.createAmenityEntry(customerId, hostelId, amenityId, request);
        return new ResponseEntity<>(Utils.REQUEST_SENT_SUCCESSFULLY, HttpStatus.OK);
    }


}
