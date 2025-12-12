package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.AmenityRequest;
import com.smartstay.tenant.response.amenity.AmenityRequestResponse;
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


    @Query("""
                SELECT new com.smartstay.tenant.response.amenity.AmenityRequestResponse(
                    ar.amenityRequestId,
                    ar.hostelId,
                    ar.customerId,
                    ar.amenityId,
                    a.amenityName,
                    ar.requestedDate,
                    ar.startFrom,
                    ar.currentStatus,
                    ar.description
                )
                FROM AmenityRequest ar
                LEFT JOIN AmenitiesV1 a ON ar.amenityId = a.amenityId
                WHERE ar.customerId = :customerId
                  AND ar.hostelId = :hostelId
                  AND ar.isActive = true
            """)
    List<AmenityRequestResponse> findRequestsForCustomer(@Param("customerId") String customerId, @Param("hostelId") String hostelId);

    @Query("""
                SELECT new com.smartstay.tenant.response.amenity.AmenityRequestResponse(
                    ar.amenityRequestId,
                    ar.hostelId,
                    ar.customerId,
                    ar.amenityId,
                    a.amenityName,
                    ar.requestedDate,
                    ar.startFrom,
                    ar.currentStatus,
                    ar.description
                )
                FROM AmenityRequest ar
                LEFT JOIN AmenitiesV1 a ON ar.amenityId = a.amenityId
                WHERE ar.customerId = :customerId
                  AND ar.amenityRequestId = :requestId
                  AND ar.hostelId = :hostelId
                  AND ar.isActive = true
            """)
    AmenityRequestResponse findRequestsForCustomerById(@Param("customerId") String customerId, @Param("hostelId") String hostelId, @Param("requestId") Long requestId);

    List<AmenityRequest> findByHostelIdAndCustomerId(String hostelId, String customerId);
}
