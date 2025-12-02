package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customers, String> {

    Customers findByCustomerId(String customerId);

    Customers findByMobileAndHostelId(String mobile, String hostelId);

    boolean existsByCustomerIdAndHostelId(String customerId, String hostelId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " + "FROM Customers c " + "WHERE c.hostelId = :hostelId " + "AND c.customerId = :customerId " + "AND c.currentStatus IN (:statuses)")
    boolean existsByHostelIdAndCustomerIdAndStatusesIn(@Param("hostelId") String hostelId, @Param("customerId") String customerId, @Param("statuses") List<String> statuses);

}
