package com.smartstay.tenant.service;


import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.AdminNotifications;
import com.smartstay.tenant.dao.CustomerNotifications;
import com.smartstay.tenant.ennum.RequestType;
import com.smartstay.tenant.ennum.UserType;
import com.smartstay.tenant.payload.amenity.RequestAmenity;
import com.smartstay.tenant.payload.bedChange.BedChangePayload;
import com.smartstay.tenant.payload.notification.MarkAsReadRequest;
import com.smartstay.tenant.response.notification.NotificationProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificationService {


    @Autowired
    private Authentication authentication;

    @Autowired
    private CustomerService customerService;


    @Autowired
    private AdminNotificationService notificationService;

    public ResponseEntity<?> getNotificationList(String hostelId) {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Utils.UNAUTHORIZED);
        }
        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        List<NotificationProjection> notifications = notificationService.getActiveNotifications(hostelId);

        if (notifications.isEmpty()) {
            return new ResponseEntity<>(Utils.NOTIFICATION_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    public ResponseEntity<?> getNotificationById(String hostelId, long notificationId) {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Utils.UNAUTHORIZED);
        }
        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        NotificationProjection notifications = notificationService.getNotificationById(hostelId, notificationId);

        if (notifications == null) {
            return new ResponseEntity<>(Utils.NOTIFICATION_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    public ResponseEntity<?> markAsRead(String hostelId, MarkAsReadRequest request) {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Utils.UNAUTHORIZED);
        }
        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        String data = markAsRead(request.notificationIds(), hostelId);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
    public ResponseEntity<?> deleteNotification(String hostelId, long id) {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Utils.UNAUTHORIZED);
        }
        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        AdminNotifications notification = notificationService.findByIdAndIsDeletedFalse(id);
        if (notification == null || !notification.getHostelId().equals(hostelId)) {
            return new ResponseEntity<>(Utils.NOTIFICATION_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        notification.setDeleted(true);
        notification.setUpdatedAt(new Date());
        notificationService.saveAdminNotification(notification);
        return new ResponseEntity<>(Utils.DELETED, HttpStatus.OK);
    }
    public void createNotificationForBedChange(String userId, String hostelId, BedChangePayload request) {

        AdminNotifications notification = new AdminNotifications();
        notification.setUserId(userId);
        notification.setNotificationType(RequestType.CHANGE_BED.name());
        if (request.title() != null && !request.title().isEmpty()) {
            notification.setTitle(request.title());
        } else {
            notification.setTitle("Bed Change Request");
        }
        if (request.description() != null && !request.description().isEmpty()) {
            notification.setDescription(request.description());
        } else {
            notification.setDescription("New bed change request submitted by customer.");
        }
        notification.setUserType(UserType.TENANT.name());
        notification.setHostelId(hostelId);
        notification.setCreatedBy(userId);
        notification.setActive(true);
        notification.setRead(false);
        notification.setDeleted(false);
        notification.setCreatedAt(new Date());
        notification.setUpdatedAt(new Date());
        if (request.bedId() != null) {
            notification.setSourceId(String.valueOf(request.bedId()));
        }
        notificationService.saveAdminNotification(notification);
    }


    public void createNotificationForInvoiceGeneration(String invoiceId,String title,String description,String customerId, String hostelId) {

        CustomerNotifications notification = new CustomerNotifications();
        notification.setUserId(customerId);
        notification.setNotificationType(RequestType.INVOICE_GENERATION.name());
        notification.setTitle(title);
        notification.setDescription(description);
        notification.setUserType(UserType.TENANT.name());
        notification.setHostelId(hostelId);
        notification.setActive(true);
        notification.setRead(false);
        notification.setDeleted(false);
        notification.setCreatedAt(new Date());
        notification.setUpdatedAt(new Date());
        notification.setSourceId(invoiceId);
        notificationService.saveCustomerNotification(notification);
    }

    public void createNotificationForAmenity(String userId, String hostelId, RequestAmenity request, String amenityId) {

        AdminNotifications notification = new AdminNotifications();
        notification.setUserId(userId);
        notification.setNotificationType(RequestType.AMENITY_REQUEST.name());
        if (request.title() != null && !request.title().isEmpty()) {
            notification.setTitle(request.title());
        } else {
            notification.setTitle("Amenity Request");
        }
        if (request.description() != null && !request.description().isEmpty()) {
            notification.setDescription(request.description());
        } else {
            notification.setDescription("New amenity request submitted by customer.");
        }
        notification.setUserType(UserType.TENANT.name());
        notification.setHostelId(hostelId);
        notification.setCreatedBy(userId);
        notification.setSourceId(amenityId);
        notification.setActive(true);
        notification.setRead(false);
        notification.setDeleted(false);
        notification.setCreatedAt(new Date());
        notification.setUpdatedAt(new Date());
        notificationService.saveAdminNotification(notification);
    }

    public String markAsRead(List<Long> notificationIds, String hostelId) {
        int updatedCount = notificationService.markNotificationsAsRead(notificationIds, hostelId);
        return updatedCount > 0 ? "Notifications marked as read" : "No notifications updated";
    }

    public void createNotificationForComplaint(String userId, String hostelId, String complaintId, String complaintTitle, String complaintDescription) {

        AdminNotifications notification = new AdminNotifications();
        notification.setUserId(userId);
        notification.setNotificationType(RequestType.COMPLAINT.name());
        notification.setTitle(complaintTitle);
        notification.setDescription(complaintDescription);
        notification.setUserType(UserType.TENANT.name());
        notification.setHostelId(hostelId);
        notification.setCreatedBy(userId);
        notification.setSourceId(complaintId);
        notification.setActive(true);
        notification.setRead(false);
        notification.setDeleted(false);
        notification.setCreatedAt(new Date());
        notification.setUpdatedAt(new Date());
        notificationService.saveAdminNotification(notification);
    }
}
