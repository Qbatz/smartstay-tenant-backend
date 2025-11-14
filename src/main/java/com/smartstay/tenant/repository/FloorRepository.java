package com.smartstay.tenant.repository;


import com.smartstay.tenant.dao.Floors;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FloorRepository extends JpaRepository<Floors, Integer> {


    Floors findByFloorIdAndHostelId(int floorId, String hostelId);
}
