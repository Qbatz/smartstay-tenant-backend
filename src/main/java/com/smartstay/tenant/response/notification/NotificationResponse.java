package com.smartstay.tenant.response.notification;

import java.util.List;

public record NotificationResponse(List<NotificationResWrapper> notifications,
                                   String hostelName,
                                   String hostelInitials,
                                   String hostelLogoUrl) {
}
