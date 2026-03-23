package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.HostelV1;
import com.smartstay.tenant.repository.HostelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HostelDuplicateService {

    @Autowired
    private HostelRepository hostelRepository;

    public HostelV1 getHostelById(String hostelId) {
        return hostelRepository.findByHostelIdAndIsActiveTrueAndIsDeletedFalse(hostelId);
    }
}
