package com.smartstay.tenant.repository;


import com.smartstay.tenant.dao.ComplaintsV1;
import com.smartstay.tenant.dto.ComplaintDTO;
import com.smartstay.tenant.dto.ComplaintDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComplaintsV1Repository extends JpaRepository<ComplaintsV1, Integer> {

    @Query("""
                SELECT new com.smartstay.tenant.dto.ComplaintDTO(
                    c.complaintId,
                    ct.complaintTypeName,
                    c.complaintDate,
                    c.description,
                    c.status,
                    CONCAT(COALESCE(u.firstName, ''), ' ', COALESCE(u.lastName, ''))
                )
                FROM ComplaintsV1 c
                JOIN ComplaintTypeV1 ct ON c.complaintTypeId = ct.complaintTypeId
                LEFT JOIN Users u ON c.assigneeId = u.userId
                WHERE c.hostelId = :hostelId
                  AND c.customerId = :customerId
                  AND c.isActive = true
                  AND c.isDeleted = false
                ORDER BY c.complaintDate DESC
            """)
    List<ComplaintDTO> findComplaintsByHostelAndCustomer(@Param("hostelId") String hostelId, @Param("customerId") String customerId, Pageable pageable);


    @Query("""
                SELECT new com.smartstay.tenant.dto.ComplaintDTO(
                    c.complaintId,
                    ct.complaintTypeName,
                    c.complaintDate,
                    c.description,
                    c.status,
                    u.firstName
                )
                FROM ComplaintsV1 c
                JOIN ComplaintTypeV1 ct ON c.complaintTypeId = ct.complaintTypeId
                LEFT JOIN Users u ON c.assigneeId = u.userId
                WHERE c.hostelId = :hostelId
                  AND c.customerId = :customerId
                  AND c.isActive = true
                  AND c.isDeleted = false
                ORDER BY c.complaintDate DESC
            """)
    Page<ComplaintDTO> getAllComplaints(@Param("hostelId") String hostelId, @Param("customerId") String customerId, Pageable pageable);


    @Query("""
                SELECT new com.smartstay.tenant.dto.ComplaintDetails(
                    c.complaintId,
                    ct.complaintTypeName,
                    c.complaintDate,
                    c.description,
                    c.status,
                    COALESCE(u.firstName, 'Unassigned'),
                    f.floorName,
                    r.roomName,
                    b.bedName,
                    CONCAT(cust.firstName, ' ', cust.lastName),
                    c.assignedDate,
                    c.createdBy,
                    h.hostelName,
                    u.mobileNo
                )
                FROM ComplaintsV1 c
                JOIN ComplaintTypeV1 ct ON c.complaintTypeId = ct.complaintTypeId
                LEFT JOIN Users u ON c.assigneeId = u.userId
                LEFT JOIN Floors f ON c.floorId = f.floorId
                LEFT JOIN Rooms r ON c.roomId = r.roomId
                LEFT JOIN Beds b ON c.bedId = b.bedId
                LEFT JOIN Customers cust ON c.customerId = cust.customerId
                LEFT JOIN HostelV1 h ON c.hostelId = h.hostelId
                WHERE c.hostelId = :hostelId
                  AND c.customerId = :customerId
                  AND c.complaintId = :complaintId
                  AND c.isActive = true
                  AND c.isDeleted = false
            """)
    ComplaintDetails getComplaintById(@Param("hostelId") String hostelId, @Param("customerId") String customerId, @Param("complaintId") Integer complaintId);


    ComplaintsV1 findByComplaintIdAndCustomerIdAndHostelId(int complaintId, String customerId, String hostelId);


    ComplaintsV1 findByComplaintIdAndHostelIdAndIsDeletedFalse(Integer complaintId, String hostelId);


}