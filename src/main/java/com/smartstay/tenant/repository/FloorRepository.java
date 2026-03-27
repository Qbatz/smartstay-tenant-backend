package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.Floors;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface FloorRepository extends JpaRepository<Floors, Integer> {

    List<Floors> findAllByFloorIdIn(Set<Integer> floorIds);
}
