package com.smartstay.tenant.repository;


import com.smartstay.tenant.dao.NotificationsV1;
import com.smartstay.tenant.response.notification.NotificationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationsV1, String> {

    @Query("SELECT n FROM NotificationsV1 n " +
            "WHERE n.userId = :userId " +
            "AND n.notificationType = :type " +
            "AND n.hostelId = :hostelId " +
            "AND n.status IN (:statusList) " +
            "AND n.isDeleted = false " +
            "AND n.isActive = true")
    NotificationsV1 findExistingRequest(
            @Param("userId") String userId,
            @Param("type") String notificationType,
            @Param("hostelId") String hostelId,
            @Param("statusList") List<String> statusList
    );

    @Query(value = "SELECT " +
            "id, " +
            "CONCAT(UCASE(LEFT(title, 1)), LCASE(SUBSTRING(title, 2))) AS title, " +
            "description, " +
            "CONCAT(UCASE(LEFT(status, 1)), LCASE(SUBSTRING(status, 2))) AS status, " +
            "CONCAT(UCASE(LEFT(notification_type, 1)), LCASE(SUBSTRING(notification_type, 2))) AS notificationType, " +
            "DATE_FORMAT(created_at, '%d/%m/%Y') AS createdDate " +
            "FROM notificationsv1 " +
            "WHERE hostel_id = :hostelId AND is_deleted = false AND is_active = true " +
            "ORDER BY created_at DESC",
            nativeQuery = true)
    List<NotificationProjection> getActiveNotifications(@Param("hostelId") String hostelId);

    @Query(value = "SELECT " +
            "id, " +
            "CONCAT(UCASE(LEFT(title, 1)), LCASE(SUBSTRING(title, 2))) AS title, " +
            "description, " +
            "CONCAT(UCASE(LEFT(status, 1)), LCASE(SUBSTRING(status, 2))) AS status, " +
            "CONCAT(UCASE(LEFT(notification_type, 1)), LCASE(SUBSTRING(notification_type, 2))) AS notificationType, " +
            "DATE_FORMAT(created_at, '%d/%m/%Y') AS createdDate " +
            "FROM notificationsv1 " +
            "WHERE hostel_id = :hostelId AND id=:id AND is_deleted = false AND is_active = true " +
            "ORDER BY created_at DESC",
            nativeQuery = true)
    NotificationProjection getNotificationById(@Param("hostelId") String hostelId, @Param("id") long id);


}
