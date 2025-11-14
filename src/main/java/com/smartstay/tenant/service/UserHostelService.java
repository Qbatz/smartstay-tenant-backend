package com.smartstay.tenant.service;

import com.smartstay.tenant.repository.UserHostelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserHostelService {

    @Autowired
    private UserHostelRepository userHostelRepo;

    public boolean checkHostelAccess(String userId, String hostelId) {
        return userHostelRepo.findByUserIdAndHostelId(userId, hostelId) != null;
    }
}
