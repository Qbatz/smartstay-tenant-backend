package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.RaiseNoticeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RaiseNoticeRequestRepository extends JpaRepository<RaiseNoticeRequest, Long> {

    boolean existsByCustomerIdAndHostelIdAndRequestStatusAndIsActiveTrueAndIsDeletedFalse(String customerId,
                                                                                          String hostelId,
                                                                                          String openRequestStatus);

    List<RaiseNoticeRequest> findAllByCustomerIdInAndHostelIdInAndRequestStatusAndIsActiveTrueAndIsDeletedFalse(Set<String> customerIds,
                                                                                                                Set<String> hostelIds,
                                                                                                                String openRequestStatus);
}
