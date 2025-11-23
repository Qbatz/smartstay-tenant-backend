package com.smartstay.tenant.payload.notification;

import java.util.List;

public record MarkAsReadRequest(
        List<Long> notificationIds
) {
}
