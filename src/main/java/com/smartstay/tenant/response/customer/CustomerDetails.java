package com.smartstay.tenant.response.customer;

import com.smartstay.tenant.dto.BookingDetailsDto;
import com.smartstay.tenant.response.hostel.HostelResponse;

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
        String displayDuration,
        String currentStatus,
        String dateOfBirth,
        String gender,

        List<AdditionalContacts> additionalContacts,
        List<CustomerJobDetailsResponse> customerJobDetails,

        CustomerKycDetails kyc,

        BookingDetailsDto bookingDetails,

        HostelResponse hostel,

        List<CustomerDocumentsResponse> kycDocuments,
        List<CustomerDocumentsResponse> checkInDocuments,
        List<CustomerDocumentsResponse> otherDocuments
) {}