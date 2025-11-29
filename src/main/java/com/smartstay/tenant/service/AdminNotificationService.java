package com.smartstay.tenant.service;


import com.smartstay.tenant.dao.AdminNotification;
import com.smartstay.tenant.repository.AdminNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminNotificationService {

    @Autowired
    private AdminNotificationRepository adminNotificationRepository;

    public void saveAdminNotification(AdminNotification adminNotification) {
        adminNotificationRepository.save(adminNotification);
    }
}
