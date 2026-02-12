package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.AmenitiesV1;
import com.smartstay.tenant.response.amenity.AmenityDetails;
import com.smartstay.tenant.response.amenity.AmenityInfoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface AmentityRepository extends JpaRepository<AmenitiesV1, String> {


    @Query(value = """
                SELECT 
                    a.amenity_id AS amenityId,
                    a.amenity_name AS amenityName,
                    a.amenity_amount AS amenityAmount,
                    a.is_pro_rate AS proRate
                FROM amenitiesv1 a
                INNER JOIN customers_amenity ca 
                    ON ca.amenity_id = a.amenity_id
                WHERE a.hostel_id = :hostelId
                  AND ca.customer_id = :customerId
                  AND ca.created_at = (
                      SELECT MAX(ca2.created_at)
                      FROM customers_amenity ca2
                      WHERE ca2.customer_id = ca.customer_id
                        AND ca2.amenity_id = ca.amenity_id
                  )
                  AND a.is_active = TRUE
                  AND a.is_deleted = FALSE
                  AND ca.start_date IS NOT NULL
                  AND ca.end_date IS NULL
            """, nativeQuery = true)
    List<AmenityInfoProjection> findCurrentlyAssignedAmenities(@Param("hostelId") String hostelId, @Param("customerId") String customerId);

    @Query(value = """
        SELECT COUNT(*) 
        FROM customers_amenity ca
        WHERE ca.customer_id = :customerId
          AND ca.amenity_id = :amenityId
          AND ca.end_date IS NULL
        """, nativeQuery = true)
    Long isAmenityAlreadyAssigned(@Param("customerId") String customerId, @Param("amenityId") String amenityId);




    @Query(value = """
                SELECT 
                    a.amenity_id AS amenityId,
                    a.amenity_name AS amenityName,
                    a.amenity_amount AS amenityAmount,
                    a.is_pro_rate AS proRate
                FROM amenitiesv1 a
                WHERE a.hostel_id = :hostelId
                  AND a.is_active = TRUE
                  AND a.is_deleted = FALSE
                  AND a.amenity_id NOT IN (
                      SELECT ca.amenity_id
                      FROM customers_amenity ca
                      WHERE ca.customer_id = :customerId
                        AND ca.created_at = (
                            SELECT MAX(ca2.created_at)
                            FROM customers_amenity ca2
                            WHERE ca2.customer_id = ca.customer_id
                              AND ca2.amenity_id = ca.amenity_id
                        )
                        AND ca.end_date IS NULL        
                        AND ca.start_date IS NOT NULL 
                  )
            """, nativeQuery = true)
    List<AmenityInfoProjection> findUnassignedAmenities(@Param("hostelId") String hostelId, @Param("customerId") String customerId);


    @Query(value = """
                  SELECT
                      a.amenity_id AS amenityId,
                      a.amenity_name AS amenityName,
                      a.amenity_amount AS amenityAmount,
                      a.is_pro_rate AS proRate,
                      a.description AS description,
                      a.terms_and_condition AS termsAndCondition,
                      a.hostel_id AS hostelId,
                  
                      CASE 
                          WHEN ca.amenity_id IS NOT NULL 
                          THEN 'true'
                          ELSE 'false'
                      END AS isAssigned,
                      ca.start_date AS amenityStartDate
                  
                  FROM amenitiesv1 a
                  
                  LEFT JOIN customers_amenity ca
                      ON ca.amenity_id = a.amenity_id
                      AND ca.customer_id = :customerId
                      AND ca.created_at = (
                          SELECT MAX(ca2.created_at)
                          FROM customers_amenity ca2
                          WHERE ca2.customer_id = :customerId
                            AND ca2.amenity_id = a.amenity_id
                      )
                      AND ca.start_date IS NOT NULL
                      AND ca.end_date IS NULL
                  
                  WHERE a.hostel_id = :hostelId
                    AND a.amenity_id = :amenityId
                    AND a.is_active = TRUE
                    AND a.is_deleted = FALSE
            """, nativeQuery = true)
    AmenityDetails findAmenityByAmenityIdAndCustomerStatus(@Param("hostelId") String hostelId, @Param("amenityId") String amenityId, @Param("customerId") String customerId);


}
