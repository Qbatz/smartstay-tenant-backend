package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.Floors;
import com.smartstay.tenant.repository.FloorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class FloorsService {

    @Autowired
    private FloorRepository  floorRepository;

    public List<Floors> findAllByFloorIdIn(Set<Integer> floorIds) {
        return floorRepository.findAllByFloorIdIn(floorIds);
    }
}
