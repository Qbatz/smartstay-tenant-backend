package com.smartstay.tenant.repository;


import com.smartstay.tenant.dao.ComplaintsV1;
import com.smartstay.tenant.dto.ComplaintDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComplaintsV1Repository extends JpaRepository<ComplaintsV1, Integer> {

    @Query("SELECT new com.smartstay.tenant.dto.ComplaintDTO(" +
            "c.complaintId, ct.complaintTypeName, c.complaintDate, c.description, c.status) " +
            "FROM ComplaintsV1 c " +
            "JOIN ComplaintTypeV1 ct ON c.complaintTypeId = ct.complaintTypeId " +
            "WHERE c.hostelId = :hostelId " +
            "AND c.customerId = :customerId " +
            "AND c.isActive = true " +
            "AND c.isDeleted = false LIMIT 5")
    List<ComplaintDTO> findComplaintsByHostelAndCustomer(
            @Param("hostelId") String hostelId,
            @Param("customerId") String customerId
    );

    @Query("SELECT new com.smartstay.tenant.dto.ComplaintDTO(" +
            "c.complaintId, ct.complaintTypeName, c.complaintDate, c.description, c.status) " +
            "FROM ComplaintsV1 c " +
            "JOIN ComplaintTypeV1 ct ON c.complaintTypeId = ct.complaintTypeId " +
            "WHERE c.hostelId = :hostelId " +
            "AND c.customerId = :customerId " +
            "AND c.isActive = true " +
            "AND c.isDeleted = false")
    List<ComplaintDTO> getAllComplaints(
            @Param("hostelId") String hostelId,
            @Param("customerId") String customerId
    );
}