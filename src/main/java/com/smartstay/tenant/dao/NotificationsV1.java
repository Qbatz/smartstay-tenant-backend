package com.smartstay.tenant.dao;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationsV1 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String userId;
    private String notificationType;
    private String description;
    private String status;
    private String title;
    private String userType;
    private Date createdAt;
    private Date updatedAt;
    private boolean isActive;
    private boolean isRead;
    private String hostelId;
    private String createdBy;
    private boolean isDeleted;
    private String amenityId;
    private String sourceId;

}
