package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.Rooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RoomRepository extends JpaRepository<Rooms, Integer> {

    List<Rooms> findAllByRoomIdIn(Set<Integer> roomIds);
}
