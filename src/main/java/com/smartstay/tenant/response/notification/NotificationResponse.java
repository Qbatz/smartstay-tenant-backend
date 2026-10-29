package com.smartstay.tenant.response.notification;

import com.smartstay.tenant.response.PaginationDataRes;

import java.util.List;

public record NotificationResponse(PaginationDataRes paginationData,
                                   List<NotificationResWrapper> notifications,
                                   String hostelName,
                                   String hostelInitials,
                                   String hostelLogoUrl) {
}
