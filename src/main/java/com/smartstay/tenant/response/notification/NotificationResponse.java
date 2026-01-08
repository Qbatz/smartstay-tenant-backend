package com.smartstay.tenant.response.notification;

import java.util.List;

public record NotificationResponse(
    List<NotificationProjection> notifications,
    String hostelName,
    String hostelInitials,
    String hostelLogoUrl
){

}
