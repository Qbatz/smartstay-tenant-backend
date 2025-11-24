package com.smartstay.tenant.payload.notification;

public record NotificationRequest(
        String title,
        String description,
        String startFrom
) {
}
