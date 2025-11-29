package com.smartstay.tenant.service;


import com.smartstay.tenant.dao.AdminNotifications;
import com.smartstay.tenant.repository.AdminNotificationRepository;
import com.smartstay.tenant.response.notification.NotificationProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminNotificationService {

    @Autowired
    private AdminNotificationRepository adminNotificationRepository;

    public void saveAdminNotification(AdminNotifications adminNotifications) {
        adminNotificationRepository.save(adminNotifications);
    }

    public List<NotificationProjection> getActiveNotifications(String hostelId) {
        return adminNotificationRepository.getActiveNotifications(hostelId);
    }

    public NotificationProjection getNotificationById(String hostelId, long id) {
        return adminNotificationRepository.getNotificationById(hostelId, id);
    }

    public int markNotificationsAsRead(List<Long> notificationIds, String hostelId) {
        return adminNotificationRepository.markNotificationsAsRead(notificationIds, hostelId);
    }

    AdminNotifications findByIdAndIsDeletedFalse(Long id) {
        return adminNotificationRepository.findByIdAndIsDeletedFalse(id).get();
    }





}
