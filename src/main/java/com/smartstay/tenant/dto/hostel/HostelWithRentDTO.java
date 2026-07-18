package com.smartstay.tenant.dto.hostel;

import com.smartstay.tenant.response.customer.CustomerHostelDocsRes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostelWithRentDTO {

    private String customerId;
    private String hostelId;
    private String hostelName;
    private String ownerId;
    private String ownerName;
    private String hostelInitial;
    private String hostelPic;
    private String hostelMobile;
    private String houseNo;
    private String street;
    private String landmark;
    private int pincode;
    private String city;
    private String state;
    private String fullAddress;
    private String currentStatus;
    private int statusCode;
    private Boolean canRaiseNotice;
    private Integer noticeDays;
    private RentalDetailsDTO rentalDetails;
    private List<CustomerHostelDocsRes> customerHostelDocs;
}
