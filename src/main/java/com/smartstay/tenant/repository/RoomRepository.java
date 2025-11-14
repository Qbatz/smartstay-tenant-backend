package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.Rooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Rooms, Integer> {

    Rooms findByRoomIdAndParentIdAndHostelId(int roomId, String parentId, String hostelId);

    Rooms findByRoomIdAndParentIdAndHostelIdAndFloorId(int roomId, String parentId, String hostelId,int floorId);


}
