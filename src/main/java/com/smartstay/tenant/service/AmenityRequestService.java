package com.smartstay.tenant.service;


import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dao.AmenityRequest;
import com.smartstay.tenant.dao.BillingRules;
import com.smartstay.tenant.dto.BillingDates;
import com.smartstay.tenant.ennum.RequestStatus;
import com.smartstay.tenant.payload.amenity.RequestAmenity;
import com.smartstay.tenant.repository.AmenityRequestRepository;
import com.smartstay.tenant.repository.BillingRuleRepository;
import com.smartstay.tenant.response.amenity.AmenityRequestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class AmenityRequestService {


    @Autowired
    private AmenityRequestRepository amenityRequestRepository;

    @Autowired
    private HostelConfigService hostelConfigService;


    public void createAmenityEntry(String customerId, String hostelId, String amenityId, RequestAmenity request) {
        AmenityRequest amenityRequest = new AmenityRequest();
        amenityRequest.setCustomerId(customerId);
        amenityRequest.setHostelId(hostelId);
        amenityRequest.setAmenityId(amenityId);
        amenityRequest.setRequestedDate(new Date());
        if (request.description() != null && !request.description().isEmpty()) {
            amenityRequest.setDescription(request.title());
        }
        if (request.startFrom() != null && request.startFrom().isEmpty()) {
            amenityRequest.setStartFrom(getLatestBillRuleByHostelIdAndStartDate(hostelId, new Date()).currentBillStartDate());
        }
        amenityRequest.setCurrentStatus(RequestStatus.OPEN.name());
        amenityRequest.setIsActive(true);
        amenityRequest.setCreatedAt(new Date());
        amenityRequest.setUpdatedAt(new Date());
        amenityRequest.setUpdatedBy(customerId);

        amenityRequestRepository.save(amenityRequest);
    }

    public BillingDates getLatestBillRuleByHostelIdAndStartDate(String hostelId, Date date) {
        BillingDates billingDates = hostelConfigService.getBillingRuleByDateAndHostelId(hostelId, new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(billingDates.currentBillStartDate());
        calendar.add(Calendar.MONTH, 1);
       return hostelConfigService.getBillingRuleByDateAndHostelId(hostelId, calendar.getTime());
    }

    public boolean existsPendingRequest(String customerId, String amenityId) {
        return amenityRequestRepository.existsPendingRequest(customerId, amenityId, List.of(RequestStatus.OPEN.name(), RequestStatus.INPROGRESS.name()));
    }

    public List<AmenityRequestResponse> getRequests(String customerId, String hostelId) {
        return amenityRequestRepository.findRequestsForCustomer(customerId, hostelId);
    }

    public AmenityRequestResponse getRequestById(String customerId, String hostelId, Long requestId) {
        return amenityRequestRepository.findRequestsForCustomerById(customerId, hostelId, requestId);
    }



}
