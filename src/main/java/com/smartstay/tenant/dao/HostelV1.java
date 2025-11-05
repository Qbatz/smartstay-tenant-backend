package com.smartstay.tenant.dao;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HostelV1 {

    @Id
    private String hostelId;
    private int hostelType;
    private String hostelName;
    private String mobile;
    private String emailId;
    private String mainImage;
    private String houseNo;
    private String street;
    private String landmark;
    private int pincode;
    private String city;
    private String state;
    private int country;
    private String parentId;
    private String createdBy;
    private Date createdAt;
    private Date updatedAt;

    @OneToMany(mappedBy = "hostel", cascade = CascadeType.ALL)
    private List<Subscription> subscription;

    @OneToMany(mappedBy = "hostel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HostelImages> additionalImages;

}
