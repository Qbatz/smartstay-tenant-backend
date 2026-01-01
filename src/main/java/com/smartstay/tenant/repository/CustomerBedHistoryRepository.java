package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.CustomersBedHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CustomerBedHistoryRepository extends JpaRepository<CustomersBedHistory, Long> {

    @Query(value = """
            SELECT * FROM customers_bed_history WHERE customer_id=:customerId and end_date IS NULL
            ORDER BY start_date DESC limit 1
            """, nativeQuery = true)
    CustomersBedHistory getLatestRentAmount(@Param("customerId") String customerId, @Param("endDate") Date endDate);

    @Query(value = """
            SELECT * FROM customers_bed_history WHERE customer_id=:customerId and start_date <= DATE(:endDate) and (end_date IS NULL OR DATE(end_date) >= DATE(:startDate))
            ORDER BY start_date ASC
            """, nativeQuery = true)
    List<CustomersBedHistory> findByDates(@Param("customerId") String customerId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = """
            SELECT * FROM customers_bed_history WHERE customer_id=:customerId and DATE(start_date) <= DATE(:endDate) and (end_date IS NULL OR DATE(end_date) >= DATE(:startDate))
            ORDER BY start_date DESC LIMIT 1
            """, nativeQuery = true)
    CustomersBedHistory findByCustomerIdAndDate(@Param("customerId") String customerId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);


    @Query(value = """
            SELECT * FROM customers_bed_history WHERE customer_id=:customerId AND type='BOOKED' LIMIT 1
            """, nativeQuery = true)
    CustomersBedHistory findByCustomerIdAndTypeBooking(@Param("customerId") String customerId);

    @Query("""
                SELECT h FROM CustomersBedHistory h
                WHERE h.customerId = :customerId
                  AND h.hostelId = :hostelId
                  AND h.isActive = true
                  AND h.startDate <= :endDate
                  AND (h.endDate IS NULL OR h.endDate >= :startDate)
                ORDER BY h.startDate ASC
            """)
    List<CustomersBedHistory> findBedHistoryInRange(@Param("customerId") String customerId, @Param("hostelId") String hostelId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);


}
