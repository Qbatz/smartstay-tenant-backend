package com.smartstay.tenant.dto.hostel;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostelWithRentDTO {

    private String customerId;
    private String hostelId;
    private String hostelName;
    private String hostelInitial;
    private String houseNo;
    private String street;
    private String landmark;
    private int pincode;
    private String city;
    private String state;
    private String hostelPic;
    private String currentStatus;
    private int statusCode;

    private RentalDetailsDTO rentalDetails;
}
