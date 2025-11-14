package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.Beds;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BedsRepository extends JpaRepository<Beds, Integer> {

    Beds findByBedIdAndRoomIdAndParentId(int bedId, int roomId, String parentId);

    Beds findByBedIdAndParentIdAndHostelId(int bedId, String parentId, String hostelId);

}
