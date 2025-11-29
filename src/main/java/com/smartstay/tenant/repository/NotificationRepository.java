package com.smartstay.tenant.repository;


import com.smartstay.tenant.dao.NotificationsV1;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationsV1, String> {


}
