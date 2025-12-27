package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.AdminNotifications;
import com.smartstay.tenant.dao.CustomerNotifications;
import com.smartstay.tenant.response.notification.NotificationProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerNotificationRepository extends JpaRepository<CustomerNotifications, Long> {


    @Query(value = """
                SELECT 
                    id, 
                    CONCAT(UCASE(LEFT(title, 1)), LCASE(SUBSTRING(title, 2))) AS title, 
                    description, 
                    CONCAT(UCASE(LEFT(notification_type, 1)), LCASE(SUBSTRING(notification_type, 2))) AS notificationType, 
                    DATE_FORMAT(created_at, '%d/%m/%Y') AS createdDate, 
                    is_read 
                FROM customer_notifications 
                WHERE hostel_id = :hostelId 
                AND user_id = :userId
                  AND is_deleted = false 
                  AND is_active = true 
                ORDER BY created_at DESC
            """, nativeQuery = true)
    List<NotificationProjection> getActiveNotifications(@Param("hostelId") String hostelId, @Param("userId") String userId);

    @Query(value = """
                SELECT 
                    id, 
                    CONCAT(UCASE(LEFT(title, 1)), LCASE(SUBSTRING(title, 2))) AS title, 
                    description, 
                    CONCAT(UCASE(LEFT(notification_type, 1)), LCASE(SUBSTRING(notification_type, 2))) AS notificationType, 
                    DATE_FORMAT(created_at, '%d/%m/%Y') AS createdDate, 
                    is_read 
                FROM customer_notifications 
                WHERE hostel_id = :hostelId 
                  AND id = :id 
                  AND is_deleted = false 
                  AND is_active = true 
                ORDER BY created_at DESC
            """, nativeQuery = true)
    NotificationProjection getNotificationById(@Param("hostelId") String hostelId, @Param("id") long id);


    @Modifying
    @Transactional
    @Query("UPDATE CustomerNotifications n SET n.isRead = true, n.updatedAt = CURRENT_TIMESTAMP " + "WHERE n.id IN :notificationIds AND n.hostelId = :hostelId AND n.isDeleted = false AND n.isActive = true")
    int markNotificationsAsRead(@Param("notificationIds") List<Long> notificationIds, @Param("hostelId") String hostelId);

    Optional<AdminNotifications> findByIdAndIsDeletedFalse(Long id);
}
