package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.ComplaintTypeV1;
import com.smartstay.tenant.response.complaints.ComplaintTypeResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComplaintTypeV1Repository extends JpaRepository<ComplaintTypeV1, Integer> {

    @Query("""
       SELECT 
           c.complaintTypeId AS complaintTypeId, 
           c.complaintTypeName AS complaintTypeName
       FROM ComplaintTypeV1 c
       WHERE c.hostelId = :hostelId
       """)
    List<ComplaintTypeResponse> getAllComplaintsType(@Param("hostelId") String hostelId);




}
