package com.smartstay.tenant.response.notification;

public interface NotificationProjection {

    long getId();
    String getTitle();
    String getDescription();
    String getStatus();
    String getNotificationType();
    String getCreatedDate();
    boolean getIsRead();
}
