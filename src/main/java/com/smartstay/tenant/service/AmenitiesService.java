package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Constants;
import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.*;
import com.smartstay.tenant.dto.BillingDates;
import com.smartstay.tenant.ennum.CustomerStatus;
import com.smartstay.tenant.mapper.amenities.AmenityRequestMapper;
import com.smartstay.tenant.mapper.amenities.AmenityResponseMapper;
import com.smartstay.tenant.payload.amenity.RequestAmenity;
import com.smartstay.tenant.repository.AmentityRepository;
import com.smartstay.tenant.repository.CustomerAmenityRepository;
import com.smartstay.tenant.response.PaginationDataRes;
import com.smartstay.tenant.response.amenity.*;
import com.smartstay.tenant.response.hostel.RequestItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AmenitiesService {

    @Autowired
    private AmentityRepository amenityRepository;
    @Autowired
    private AmenityRequestService amenityRequestService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private Authentication authentication;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private HostelConfigService hostelConfigService;
    @Autowired
    private CustomerAmenityRepository customerAmenityRepository;
    @Autowired
    private FCMNotificationService fcmNotificationService;

    public ResponseEntity<?> getAllAmenities(String hostelId) {

        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        List<AmenityInfoProjection> assigned = amenityRepository
                .findCurrentlyAssignedAmenities(hostelId, customerId);
        List<AmenityInfoProjection> unassigned = amenityRepository
                .findUnassignedAmenities(hostelId, customerId);

        AmenitiesStatusResponse response = new AmenitiesStatusResponse(
                assigned != null ? assigned : List.of(),
                unassigned != null ? unassigned : List.of());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> getAmenityRequest(String hostelId, int page, int size) {

        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        String customerId = authentication.getName();
        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        page = Math.max(page - 1, 0);
        size = Math.max(size, 1);

        Pageable pageable = PageRequest.of(page, size);

        Page<AmenityRequest> pagedAmenityRequests = amenityRequestService
                .getPagedAmenityRequestsByCustomerIdAndHostelId(customerId, hostelId, pageable);

        List<AmenityRequest> amenityRequests = pagedAmenityRequests.getContent();

        Set<String> amenityIds = amenityRequests.stream()
                .map(AmenityRequest::getAmenityId)
                .collect(Collectors.toSet());

        List<AmenitiesV1> amenities = amenityRepository
                .findAllByAmenityIdInAndIsActiveTrueAndIsDeletedFalse(amenityIds);

        List<RequestItemResponse> amenityRequestsRes = amenityRequests.stream()
                .map(i -> new AmenityRequestMapper(amenities)
                        .apply(i))
                .toList();

        PaginationDataRes paginationDataRes = new PaginationDataRes(page + 1, size,
                pagedAmenityRequests.getTotalElements(), pagedAmenityRequests.getTotalPages());

        AmenityRequestResWrapper response = new AmenityRequestResWrapper(paginationDataRes, amenityRequestsRes);

        return new ResponseEntity<>(response, HttpStatus.OK);
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

        AmenityRequest amenityRequest = amenityRequestService
                .getAmenityRequestByCustomerIdAndAmenityId(customerId, amenityId);

        if (amenityInfo != null) {

            AmenityResponseMapper amenityResponseMapper = new AmenityResponseMapper(hostelConfigService, amenityRequest);

            AmenityDetailsResponse amenityDetailsResponse = amenityResponseMapper.apply(amenityInfo);

            return new ResponseEntity<>(amenityDetailsResponse, HttpStatus.OK);
        }

        return new ResponseEntity<>(Utils.NO_RECORDS_FOUND, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> createAmenityRequest(String hostelId, String amenityId, RequestAmenity request) {

        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        String tenantPlatform = authentication.getPlatform();

        String customerId = authentication.getName();
        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        if (amenityRequestService.existsPendingRequest(customerId, amenityId)) {
            return new ResponseEntity<>("Already requested. Please wait for approval.", HttpStatus.BAD_REQUEST);
        }

        List<String> currentStatus = Arrays.asList(CustomerStatus.CHECK_IN.name(), CustomerStatus.NOTICE.name());

        boolean customerExist = customerService
                .existsByHostelIdAndCustomerIdAndStatusesIn(hostelId, customerId, currentStatus);
        if (!customerExist) {
            return new ResponseEntity<>(Utils.CUSTOMER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        Long count = amenityRepository.isAmenityAlreadyAssigned(customerId, amenityId);
        if (count != null && count > 0) {
            return new ResponseEntity<>("Amenity already assigned to this customer.", HttpStatus.BAD_REQUEST);
        }

        Customers customer = customerService.getCustomerInformation(customerId);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Utils.CUSTOMER_NOT_FOUND);
        }

        amenityRequestService.createAmenityEntry(customerId, hostelId, amenityId, request, tenantPlatform);
        notificationService.createNotificationForAmenity(customerId, hostelId, request, amenityId);
        fcmNotificationService.sendAmenityRequestNotification(hostelId, customer);

        return new ResponseEntity<>(Utils.REQUEST_SENT_SUCCESSFULLY, HttpStatus.OK);
    }

    public List<AmenitiesV1> findByAmenityIds(List<String> listAmenitiesId) {
        return amenityRepository.findAllById(listAmenitiesId);
    }

    public ResponseEntity<?> deleteRequestById(String hostelId, long requestId) {

        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        AmenityRequest amenityRequest = amenityRequestService
                .getAmenityRequestById(requestId);
        if (amenityRequest == null) {
            return new ResponseEntity<>(Constants.AMENITY_REQUEST_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        amenityRequestService.delete(amenityRequest);

        return new ResponseEntity<>(Constants.REQUEST_REMOVED_SUCCESSFULLY, HttpStatus.OK);
    }

    public ResponseEntity<?> deactivateAmenity(String hostelId, String amenityId) {

        String customerId = authentication.getName();
        String tenantPlatform = authentication.getPlatform();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        AmenitiesV1 amenity = amenityRepository
                .findByAmenityIdAndIsActiveTrueAndIsDeletedFalse(amenityId);
        if (amenity == null){
            return new ResponseEntity<>(Constants.AMENITY_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        CustomersAmenity customersAmenity = customerAmenityRepository
                .findTopByAmenityIdAndCustomerIdAndEndDateIsNullOrderByCreatedAtDesc(amenityId, customerId);
        if (customersAmenity == null){
            return new ResponseEntity<>(Constants.AMENITY_IS_NOT_ASSIGNED, HttpStatus.BAD_REQUEST);
        }

        Date today = new Date();

        Date endDate = null;
        if (amenity.getIsProRate()){
            endDate = today;
        } else {
            BillingRules billingRules = hostelConfigService.getCurrentMonthTemplate(hostelId);
            BillingDates billingDates = hostelConfigService.computeBillingDates(billingRules, today);
            endDate = billingDates != null ? billingDates.currentBillEndDate() : today;
        }

        customersAmenity.setEndDate(endDate != null ? endDate : today);
        customersAmenity.setUpdatedBy(authentication.getName());
        customersAmenity.setUpdatedAt(today);
        customersAmenity.setTenantPlatform(tenantPlatform);

        customerAmenityRepository.save(customersAmenity);

        return new ResponseEntity<>(Constants.DEACTIVATED, HttpStatus.OK);
    }
}
