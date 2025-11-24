package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.BedChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BedChangeRequestRepo extends JpaRepository<BedChangeRequest, Long> {

    boolean existsByCustomerIdAndHostelIdAndIsActiveTrueAndIsDeletedFalseAndCurrentStatusIn(
            String customerId,
            String hostelId,
            List<String> statusList
    );

}
