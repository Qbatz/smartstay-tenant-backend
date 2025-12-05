package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.UserHostel;
import com.smartstay.tenant.repository.UserHostelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserHostelService {

    @Autowired
    private UserHostelRepository userHostelRepo;

    public UserHostel findByUserIdAndHostelId(String userId, String hostelId) {
        return userHostelRepo.findByUserIdAndHostelId(userId, hostelId);
    }
}
