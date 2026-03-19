package com.smartstay.tenant.response.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartstay.tenant.dto.BookingDetailsDto;
import com.smartstay.tenant.response.hostel.HostelResponse;

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

        List<AdditionalContacts> additionalContacts,

        CustomerKycDetails kyc,

        BookingDetailsDto bookingDetails,

        HostelResponse hostel,

        List<CustomerDocumentsResponse> kycDocuments,
        List<CustomerDocumentsResponse> checkInDocuments,
        List<CustomerDocumentsResponse> otherDocuments
) {}