package com.smartstay.tenant.response.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartstay.tenant.dto.BookingDetailsDto;

import java.util.Date;
import java.util.List;

public record CustomerDetails(

        String customerId,
        String firstName,
        String lastName,
        String emailId,
        String mobile,
        String houseNo,
        String street,
        String landmark,
        int pincode,
        String city,
        String state,
        String profilePic,

        String initials,

        String expJoiningDate,
        String currentStatus,

        @JsonFormat(pattern = "dd/MM/yyyy")
        Date dateOfBirth,

        String gender,

        CustomerKycDetails kyc,

        BookingDetailsDto bookingDetails,

        List<CustomerDocumentsResponse> customerDocuments
) {}