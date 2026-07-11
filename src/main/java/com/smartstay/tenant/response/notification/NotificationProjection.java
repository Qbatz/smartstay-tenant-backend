package com.smartstay.tenant.response.notification;

public interface NotificationProjection {
    long getId();
    String getTitle();
    String getDescription();
    String getNotificationType();
    String getFullNotificationType();
    String getCreatedDate();
    boolean getIsRead();
}
