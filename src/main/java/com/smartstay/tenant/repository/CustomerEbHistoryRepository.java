package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.CustomersEbHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CustomerEbHistoryRepository extends JpaRepository<CustomersEbHistory, Long> {

    @Query("""
            SELECT CER
            FROM CustomersEbHistory CER
            WHERE CER.customerId=:customerId
            AND CER.startDate >= :startDate
            AND CER.endDate <= :endDate
            """)
    List<CustomersEbHistory> getAllByCustomerIdAndDateBetween(@Param("customerId") String customerId,
                                                              @Param("startDate") Date startDate,
                                                              @Param("endDate") Date endDate);
}
