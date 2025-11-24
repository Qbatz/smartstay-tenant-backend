package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.AmenityRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AmenityRequestRepository extends JpaRepository<AmenityRequest, Long> {


    @Query("""
                SELECT COUNT(ar) > 0 
                FROM AmenityRequest ar
                WHERE ar.customerId = :customerId
                  AND ar.amenityId = :amenityId
                  AND ar.currentStatus IN (:currentStatus)
                  AND ar.isActive = TRUE
            """)
    boolean existsPendingRequest(@Param("customerId") String customerId, @Param("amenityId") String amenityId, @Param("currentStatus") List<String> currentStatus);


}
