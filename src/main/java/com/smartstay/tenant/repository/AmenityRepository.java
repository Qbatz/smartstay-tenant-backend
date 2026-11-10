package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.AmenitiesV1;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface AmenityRepository extends JpaRepository<AmenitiesV1, String> {

    AmenitiesV1 findByAmenityIdAndIsActiveTrueAndIsDeletedFalse(String amenityId);

    List<AmenitiesV1> findAllByAmenityIdInAndIsActiveTrueAndIsDeletedFalse(Set<String> amenityIds);

    List<AmenitiesV1> findAllByHostelIdAndIsActiveTrueAndIsDeletedFalse(String hostelId);
}
