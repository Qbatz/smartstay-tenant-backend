package com.smartstay.tenant.dao;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subscriptionId;
    private String subscriptionNumber;
    private String hostelId;
    private String planCode;
    private String planName;
    private Date planStartsAt;
    private Date planEndsAt;
    private Date activatedAt;
    private Double paidAmount;
    private Double planAmount;
    private Double discount;
    private Double discountAmount;
    private Date nextBillingAt;
    private Date createdAt;

}
