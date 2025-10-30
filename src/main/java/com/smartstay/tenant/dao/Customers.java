package com.smartstay.tenant.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customers {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String customerId;
    private String firstName;
    private String lastName;
    private String mobile;
    private String emailId;
    private String houseNo;
    private String street;
    private String landmark;
    private int pincode;
    private String city;
    private String state;
    private Long country;
    private String profilePic;
    private String customerBedStatus;
    private Date joiningDate;
    private Date expJoiningDate;
    private String currentStatus;
    private String kycStatus;
    private String createdBy;
    private String hostelId;
    private Date createdAt;
    private Date lastUpdatedAt;
    private String updatedBy;
    private String mobSerialNo;

    @OneToOne(mappedBy = "customers", cascade = CascadeType.ALL, orphanRemoval = true)
    private Advance advance;

    @OneToOne(mappedBy = "customers", cascade = CascadeType.ALL, orphanRemoval = true)
    private KycDetails kycDetails;

    @OneToOne(mappedBy = "customers", cascade = CascadeType.ALL, orphanRemoval = true)
    CustomerWallet wallet;

    @OneToOne(mappedBy = "customers", cascade = CascadeType.ALL, orphanRemoval = true)
    private Reasons reasons;

}
