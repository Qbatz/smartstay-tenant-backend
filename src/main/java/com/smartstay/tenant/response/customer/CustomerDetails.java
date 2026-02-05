package com.smartstay.tenant.response.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartstay.tenant.dto.BookingDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


public record CustomerDetails(

        String customerId,
        String firstName,
        String lastName,
        String houseNo,
        String street,
        String landmark,
        int pincode,
        String city,
        String state,
        String profilePic,

        String initials,

        String expJoiningDate,

        @JsonFormat(pattern = "dd/MM/yyyy")
        Date dateOfBirth,

        String gender,

        CustomerKycDetails kyc,

        BookingDetailsDto bookingDetails
) {}

