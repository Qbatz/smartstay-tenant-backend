package com.smartstay.tenant.service;


import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dao.BedChangeRequest;
import com.smartstay.tenant.dto.BedChangeRequestResponse;
import com.smartstay.tenant.ennum.RequestStatus;
import com.smartstay.tenant.mapper.bed.BedChangeRequestMapper;
import com.smartstay.tenant.payload.bedChange.BedChangePayload;
import com.smartstay.tenant.repository.BedChangeRequestRepo;
import com.smartstay.tenant.response.hostel.RequestItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BedChangeRequestService {


    @Autowired
    private BedChangeRequestRepo requestRepo;


    public boolean existsPendingRequest(String customerId, String hostelId) {
        return requestRepo.existsByCustomerIdAndHostelIdAndIsActiveTrueAndIsDeletedFalseAndCurrentStatusIn(
                customerId,
                hostelId,
                List.of(RequestStatus.PENDING.name(), RequestStatus.INPROGRESS.name()
        ));
    }

    public List<RequestItemResponse> getRequests(String hostelId, String customerId) {
        List<BedChangeRequest> listBedChangeReguest = requestRepo.findByHostelIdAndCustomerId(hostelId, customerId);

        return listBedChangeReguest.stream()
                .map(i -> new BedChangeRequestMapper().apply(i))
                .toList();
    }

    public BedChangeRequestResponse getRequestsById(String hostelId, String customerId, Long requestId) {
        return requestRepo.findBedChangeRequestsById(hostelId, customerId, requestId);
    }


    public BedChangeRequest saveBedChangeRequest(String hostelId, String customerId, BedChangePayload request) {
        BedChangeRequest bedRequest = new BedChangeRequest();
        bedRequest.setHostelId(hostelId);
        bedRequest.setCustomerId(customerId);
        if (request.bedId() != null) {
            bedRequest.setBedId(request.bedId());
        }
        if (request.floorId() != null) {
            bedRequest.setFloorId(request.floorId());
        }
        if (request.roomId() != null) {
            bedRequest.setRoomId(request.roomId());
        }
        bedRequest.setCurrentStatus(RequestStatus.OPEN.name());

        if (request.startFrom() != null) {
            bedRequest.setStartsFrom(Utils.addDaysToDate(new Date(), request.startFrom()));
        }
        if (request.reason() != null) {
            bedRequest.setReason(request.reason());
        }
        if (request.preferredType() != null) {
            bedRequest.setPreferredType(request.preferredType());
        }
        bedRequest.setCreatedAt(new Date());
        bedRequest.setActive(true);
        bedRequest.setDeleted(false);
        return requestRepo.save(bedRequest);
    }





}
