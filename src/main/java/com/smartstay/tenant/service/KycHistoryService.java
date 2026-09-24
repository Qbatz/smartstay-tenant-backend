package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.KycHistory;
import com.smartstay.tenant.repository.KycHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KycHistoryService {

    @Autowired
    private KycHistoryRepository kycHistoryRepository;

    public KycHistory getLatestByHostelId(String hostelId) {
        return kycHistoryRepository.findTopByHostelIdOrderByHistoryIdDesc(hostelId);
    }
}
