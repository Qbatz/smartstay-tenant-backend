package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.BedChangeRequest;
import com.smartstay.tenant.dao.Beds;
import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.dto.BedDetails;
import com.smartstay.tenant.ennum.BedChangeUrgencyEnum;
import com.smartstay.tenant.ennum.CustomerStatus;
import com.smartstay.tenant.mapper.bed.BedChangeRequestMapper;
import com.smartstay.tenant.payload.bedChange.BedChangePayload;
import com.smartstay.tenant.repository.BedsRepository;
import com.smartstay.tenant.response.PaginationDataRes;
import com.smartstay.tenant.response.bed.BedChangeRequestResWrapper;
import com.smartstay.tenant.response.bed.BedChangeUrgencyDropdownRes;
import com.smartstay.tenant.response.hostel.RequestItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
    @Autowired
    private FCMNotificationService fcmNotificationService;

    public ResponseEntity<?> requestBedChange(String hostelId, BedChangePayload request) {

        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        String tenantPlatform = authentication.getPlatform();

        String customerId = authentication.getName();
        Customers customers = customerService.getCustomerInformation(customerId);
        if (customers == null) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        if (customers.getCurrentStatus().equalsIgnoreCase(CustomerStatus.NOTICE.name())) {
            return new ResponseEntity<>(Utils.CANNOT_MAKE_BED_CHANGE_REQUEST_NOTICE_CUSTOMER, HttpStatus.BAD_REQUEST);
        }

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

        bedChangeRequestService.saveBedChangeRequest(hostelId, customerId, request, tenantPlatform);
        notificationService.createNotificationForBedChange(customerId, hostelId, request);
        fcmNotificationService.sendNotificationBedChangeRequest(hostelId);

        return new ResponseEntity<>(Utils.REQUEST_SENT_SUCCESSFULLY, HttpStatus.OK);
    }

    public ResponseEntity<?> getBedRequests(String hostelId, int page, int size) {

        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        String customerId = authentication.getName();
        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        List<String> currentStatus = Arrays.asList(CustomerStatus.CHECK_IN.name(),
                CustomerStatus.NOTICE.name());

        boolean customerExist = customerService.existsByHostelIdAndCustomerIdAndStatusesIn(
                hostelId, customerId, currentStatus);
        if (!customerExist) {
            return new ResponseEntity<>(Utils.CUSTOMER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        page = Math.max(page - 1, 0);
        size = Math.max(size, 1);

        Pageable pageable = PageRequest.of(page, size);

        Page<BedChangeRequest> pagedBedChangeRequests = bedChangeRequestService
                .getPagedBedChangeRequests(hostelId, customerId, pageable);

        List<BedChangeRequest> bedChangeRequests = pagedBedChangeRequests.getContent();

        List<RequestItemResponse> bedChangeRequestRes = bedChangeRequests.stream()
                .map(i -> new BedChangeRequestMapper(bedsRepository)
                        .apply(i))
                .toList();

        PaginationDataRes paginationDataRes = new PaginationDataRes(page + 1, size,
                pagedBedChangeRequests.getTotalElements(), pagedBedChangeRequests.getTotalPages());

        BedChangeRequestResWrapper response = new BedChangeRequestResWrapper(paginationDataRes, bedChangeRequestRes);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public BedDetails getBedDetails(Integer bedId) {
        return bedsRepository.findBedDetailsByBedId(bedId);
    }

    public List<Beds> findAllByBedIdIn(Set<Integer> bedIds) {
        return bedsRepository.findAllByBedIdIn(bedIds);
    }

    public Beds getByBedId(Integer bedId) {
        if (bedId == null){
            return null;
        }
        return bedsRepository.findByBedId(bedId);
    }

    public void save(Beds bed) {
        bedsRepository.save(bed);
    }

    public ResponseEntity<?> getBedChangeUrgency() {

        List<BedChangeUrgencyDropdownRes> response = Arrays.stream(BedChangeUrgencyEnum.values())
                .map(i -> new BedChangeUrgencyDropdownRes(
                        i.name(), i.getValue()
                ))
                .toList();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
