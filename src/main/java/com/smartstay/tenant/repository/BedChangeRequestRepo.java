package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.BedChangeRequest;
import com.smartstay.tenant.dto.BedChangeRequestResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BedChangeRequestRepo extends JpaRepository<BedChangeRequest, Long> {

    boolean existsByCustomerIdAndHostelIdAndIsActiveTrueAndIsDeletedFalseAndCurrentStatusIn(
            String customerId,
            String hostelId,
            List<String> statusList
    );

    @Query("""
    SELECT new com.smartstay.tenant.dto.BedChangeRequestResponse(
        b.id,
        b.bedId,
        bed.bedName,
        b.floorId,
        f.floorName,
        b.roomId,
        r.roomName,
        b.startsFrom,
        b.reason,
        b.preferredType,
        b.currentStatus
    )
    FROM BedChangeRequest b
    LEFT JOIN Beds bed ON b.bedId = bed.bedId AND bed.isActive = true AND bed.isDeleted = false
    LEFT JOIN Floors f ON b.floorId = f.floorId AND f.isActive = true AND f.isDeleted = false
    LEFT JOIN Rooms r ON b.roomId = r.roomId AND r.isActive = true AND r.isDeleted = false
    WHERE b.hostelId = :hostelId
      AND b.customerId = :customerId
      AND b.isActive = true
      AND b.isDeleted = false
    ORDER BY b.createdAt DESC
""")
    List<BedChangeRequestResponse> findBedChangeRequests(
            @Param("hostelId") String hostelId,
            @Param("customerId") String customerId
    );


}
