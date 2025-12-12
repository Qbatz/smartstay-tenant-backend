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
public class AmenityRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long amenityRequestId;
    String hostelId;
    String customerId;
    String amenityId;
    Date requestedDate;
    Date startFrom;
    //from request status enum
    String currentStatus;
    Boolean isActive;
    String updatedBy;
    String description;
    Date createdAt;
    Date updatedAt;
}
