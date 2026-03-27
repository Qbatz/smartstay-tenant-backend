package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.Rooms;
import com.smartstay.tenant.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class RoomsService {

    @Autowired
    private RoomRepository roomRepository;

    public List<Rooms> findAllByRoomIdIn(Set<Integer> roomIds) {
        return roomRepository.findAllByRoomIdIn(roomIds);
    }
}
