package com.smartstay.tenant.response.notification;

import com.smartstay.tenant.response.kyc.NotificationKycInfo;

public record NotificationResWrapper(long id,
                                     String title,
                                     String description,
                                     String notificationType,
                                     String fullNotificationType,
                                     String createdDate,
                                     boolean isRead,
                                     boolean isKycRequest,
                                     NotificationKycInfo kycInfo) {
}
