package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.HostelV1;
import com.smartstay.tenant.response.customer.CustomerHostels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HostelRepository extends JpaRepository<HostelV1, String> {

    @Query(value = """
                SELECT c.customer_id AS customerId,
                                        h.hostel_id AS hostelId,
                                       h.hostel_name AS hostelName,
                                       UPPER(SUBSTRING(COALESCE(h.hostel_name, ''), 1, 1)) AS hostelInitial,
                                       h.house_no AS houseNo,
                                       h.street AS street,
                                       h.landmark AS landmark,
                                       h.pincode AS pincode,
                                       h.city AS city,
                                       h.state AS state,
                                       h.main_image as hostelPic,
                                       c.current_status AS currentStatus,
                    CASE 
                    WHEN c.current_status = 'ACTIVE' THEN 1
                    WHEN c.current_status = 'INACTIVE' THEN 2
                    WHEN c.current_status = 'VACATED' THEN 3
                    WHEN c.current_status = 'NOTICE' THEN 4
                    WHEN c.current_status = 'BOOKED' THEN 5
                    WHEN c.current_status = 'CHECK_IN' THEN 6
                    WHEN c.current_status = 'WALKED_IN' THEN 7
                    WHEN c.current_status = 'CANCELLED_BOOKING' THEN 8
                    WHEN c.current_status = 'SETTLEMENT_GENERATED' THEN 9
                    ELSE 0
                END AS statusCode
                                FROM customers c
                                INNER JOIN hostelv1 h
                                WHERE c.customer_id = :customerId and h.hostel_id = c.hostel_id
            """, nativeQuery = true)
    List<CustomerHostels> findHostels(String customerId);


    @Query(value = """
            SELECT 
                c.customer_id AS customerId,
                h.hostel_id AS hostelId,
                h.hostel_name AS hostelName,
                UPPER(SUBSTRING(COALESCE(h.hostel_name, ''), 1, 1)) AS hostelInitial,
                h.house_no AS houseNo,
                h.street AS street,
                h.landmark AS landmark,
                h.pincode AS pincode,
                h.city AS city,
                h.state AS state,
                h.main_image AS hostelPic,
                c.current_status AS currentStatus,
                CASE 
                    WHEN c.current_status = 'ACTIVE' THEN 1
                    WHEN c.current_status = 'INACTIVE' THEN 2
                    WHEN c.current_status = 'VACATED' THEN 3
                    WHEN c.current_status = 'NOTICE' THEN 4
                    WHEN c.current_status = 'BOOKED' THEN 5
                    WHEN c.current_status = 'CHECK_IN' THEN 6
                    WHEN c.current_status = 'WALKED_IN' THEN 7
                    WHEN c.current_status = 'CANCELLED_BOOKING' THEN 8
                    WHEN c.current_status = 'SETTLEMENT_GENERATED' THEN 9
                    ELSE 0
                END AS statusCode
            FROM customers c
            INNER JOIN hostelv1 h ON h.hostel_id = c.hostel_id and c.current_status not in ('VACATED')
            WHERE c.mobile = :mobile
            """, nativeQuery = true)
    List<CustomerHostels> findHostelsByMobile(String mobile);


    HostelV1 findByHostelIdAndIsActiveTrueAndIsDeletedFalse(String hostelId);

    HostelV1 findByHostelId(String hostelId);


}
