package com.smartstay.tenant.controller;

import com.smartstay.tenant.payload.notification.MarkAsReadRequest;
import com.smartstay.tenant.payload.notification.NotificationRequest;
import com.smartstay.tenant.service.NotificationService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v2/notifications")
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@SecurityRequirement(name = "Authorization")
@CrossOrigin("*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;


    @GetMapping("all-notifications/{hostelId}")
    public ResponseEntity<?> getNotificationsList(@PathVariable String hostelId) {
        return notificationService.getNotificationList(hostelId);
    }

    @GetMapping("/{hostelId}/{notificationId}")
    public ResponseEntity<?> getNotification(@PathVariable String hostelId,@PathVariable long notificationId) {
        return notificationService.getNotificationById(hostelId,notificationId);
    }

    @PostMapping("/mark-as-read/{hostelId}")
    public ResponseEntity<?> markAsRead(@PathVariable("hostelId") String hostelId, @Valid @RequestBody MarkAsReadRequest request) {
        return notificationService.markAsRead(hostelId, request);
    }

    @DeleteMapping("/{hostelId}/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable("hostelId") String hostelId, @PathVariable("notificationId")Long notificationId) {
        return notificationService.deleteNotification(hostelId, notificationId);
    }



}
