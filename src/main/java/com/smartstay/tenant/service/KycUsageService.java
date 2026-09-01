package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.KYCUsage;
import com.smartstay.tenant.repository.KycUsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KycUsageService {

    @Autowired
    private KycUsageRepository kycUsageRepository;

    public KYCUsage getByHostelId(String hostelId) {
        return kycUsageRepository.findByHostelId(hostelId);
    }

    public void save(KYCUsage kycUsage) {
        kycUsageRepository.save(kycUsage);
    }
}
