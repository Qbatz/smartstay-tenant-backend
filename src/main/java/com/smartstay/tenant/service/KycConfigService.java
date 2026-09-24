package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.KycConfig;
import com.smartstay.tenant.repository.KycConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KycConfigService {

    @Autowired
    private KycConfigRepository kycConfigRepository;

    public KycConfig getByHostelId(String hostelId) {
        return kycConfigRepository.findByHostelId(hostelId);
    }
}
