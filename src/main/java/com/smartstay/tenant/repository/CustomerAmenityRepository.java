package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.CustomersAmenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface CustomerAmenityRepository extends JpaRepository<CustomersAmenity, String> {

    CustomersAmenity findTopByAmenityIdAndCustomerIdAndEndDateIsNullOrderByCreatedAtDesc(String amenityId, String customerId);

    @Query("""
            select ca from CustomersAmenity ca
            where ca.amenityId = :amenityId
                and ca.customerId = :customerId
                and ca.startDate is not null
                and (ca.endDate is null or Date(ca.endDate) > Date(:date))
            order by ca.createdAt desc
            limit 1
            """)
    CustomersAmenity findByEndDateAndAmenityIdAndCustomerId(Date date, String amenityId, String customerId);

    @Query("""
            SELECT ca FROM CustomersAmenity ca
            WHERE ca.amenityId IN :amenityIds
              AND ca.customerId = :customerId
              AND ca.startDate IS NOT NULL
              AND (ca.endDate IS NULL OR DATE(ca.endDate) > DATE(:date))
              AND ca.createdAt = (
                  SELECT MAX(ca2.createdAt)
                  FROM CustomersAmenity ca2
                  WHERE ca2.amenityId = ca.amenityId
                    AND ca2.customerId = ca.customerId
                    AND ca2.startDate IS NOT NULL
                    AND (ca2.endDate IS NULL OR DATE(ca2.endDate) > DATE(:date))
              )
            ORDER BY ca.createdAt DESC
            """)
    List<CustomersAmenity> findLatestByEndDateAndCustomerIdAndAmenityIdIn(Date date, String customerId, Set<String> amenityIds);
}
