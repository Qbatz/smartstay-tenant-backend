package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.RaiseNoticeRequest;
import com.smartstay.tenant.ennum.RequestStatus;
import com.smartstay.tenant.repository.RaiseNoticeRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class RaiseNoticeRequestService {

    @Autowired
    private RaiseNoticeRequestRepository raiseNoticeRequestRepository;

    public RaiseNoticeRequest save(RaiseNoticeRequest raiseNoticeRequest){
        return raiseNoticeRequestRepository.save(raiseNoticeRequest);
    }

    public boolean existsPendingRequest(String customerId, String hostelId){
        String openRequestStatus = RequestStatus.OPEN.name();

        return raiseNoticeRequestRepository
                .existsByCustomerIdAndHostelIdAndRequestStatusAndIsActiveTrueAndIsDeletedFalse(
                        customerId, hostelId, openRequestStatus
                );
    }

    public List<RaiseNoticeRequest> getByHostelIdsAndCustomerIds(Set<String> hostelIds,
                                                                 Set<String> customerIds) {
        String openRequestStatus = RequestStatus.OPEN.name();

        return raiseNoticeRequestRepository
                .findAllByCustomerIdInAndHostelIdInAndRequestStatusAndIsActiveTrueAndIsDeletedFalse(
                        customerIds, hostelIds, openRequestStatus
                );
    }
}
