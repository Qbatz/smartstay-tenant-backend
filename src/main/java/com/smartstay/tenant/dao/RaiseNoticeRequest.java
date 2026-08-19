package com.smartstay.tenant.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaiseNoticeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String hostelId;
    private String customerId;
    private int noticePeriodDays;
    private Date requestedDate;
    private Date checkoutDate;
    // from notice reason enum
    private String reason;
    private String remarks;
    // from request status enum
    private String requestStatus;
    private Date createdAt;
    private String createdBy;
    // from user type enum
    private String updatedByUserType;
    private Date updatedAt;
    private String updatedBy;
    private boolean isActive = true;
    private boolean isDeleted = false;
}
