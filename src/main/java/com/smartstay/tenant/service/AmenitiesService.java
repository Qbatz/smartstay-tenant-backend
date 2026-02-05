package com.smartstay.tenant.service;


import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.AmenitiesV1;
import com.smartstay.tenant.ennum.CustomerStatus;
import com.smartstay.tenant.payload.amenity.RequestAmenity;
import com.smartstay.tenant.repository.AmentityRepository;
import com.smartstay.tenant.repository.CustomerAmenityRepository;
import com.smartstay.tenant.response.amenity.AmenitiesStatusResponse;
import com.smartstay.tenant.response.amenity.AmenityDetails;
import com.smartstay.tenant.response.amenity.AmenityInfoProjection;
import com.smartstay.tenant.response.amenity.AmenityRequestResponse;
import com.smartstay.tenant.response.hostel.RequestItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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
        List<RequestItemResponse> requests = amenityRequestService.getRequests(customerId, hostelId);

        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    public ResponseEntity<?> getAmenityByAmenityId(String hostelId, String amenityId) {
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        AmenityDetails amenityInfo = amenityRepository
                .findAmenityByAmenityIdAndCustomerStatus(hostelId, amenityId, customerId);
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

        if (amenityRequestService.existsPendingRequest(customerId, amenityId)) {
            return new ResponseEntity<>("Already requested. Please wait for approval.", HttpStatus.BAD_REQUEST);
        }
        List<String> currentStatus = Arrays.asList(CustomerStatus.CHECK_IN.name(), CustomerStatus.NOTICE.name());
        boolean customerExist = customerService.existsByHostelIdAndCustomerIdAndStatusesIn(hostelId, customerId, currentStatus);
        if (!customerExist) {
            return new ResponseEntity<>(Utils.CUSTOMER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        Long count = amenityRepository.isAmenityAlreadyAssigned(customerId, amenityId);
        if (count != null && count > 0) {
            return new ResponseEntity<>("Amenity already assigned to this customer.", HttpStatus.BAD_REQUEST);
        }
        notificationService.createNotificationForAmenity(customerId, hostelId, request, amenityId);
        amenityRequestService.createAmenityEntry(customerId, hostelId, amenityId, request);
        return new ResponseEntity<>(Utils.REQUEST_SENT_SUCCESSFULLY, HttpStatus.OK);
    }


    public List<AmenitiesV1> findByAmenityIds(List<String> listAmenitiesId) {
        return amenityRepository.findAllById(listAmenitiesId);
    }
}
