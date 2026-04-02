package com.smartstay.tenant.mapper;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dao.CustomerAdditionalContacts;
import com.smartstay.tenant.dao.CustomerDocuments;
import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.dao.HostelV1;
import com.smartstay.tenant.dto.BookingDetailsDto;
import com.smartstay.tenant.ennum.DocumentType;
import com.smartstay.tenant.response.customer.*;
import com.smartstay.tenant.response.hostel.HostelResponse;

import java.util.ArrayList;
import java.util.List;

public class CustomerMapper {

    public CustomerDetails toDetailsDto(Customers c, List<CustomerAdditionalContacts> additionalContacts,
                                        CustomersBookingDetails b, HostelV1 hostel,
                                        List<CustomerDocuments> documents) {

        BookingDetailsDto bookingDto = null;

        StringBuilder initials = getInitials(c);

        List<AdditionalContacts> additionalContactsList = new ArrayList<>();
        if (additionalContacts != null && !additionalContacts.isEmpty()) {
            additionalContactsList = additionalContacts.stream()
                    .map(additionalContact -> new AdditionalContacts(
                            additionalContact.getContactId(), additionalContact.getName(),
                            additionalContact.getRelationship(), additionalContact.getMobile(),
                            additionalContact.getOccupation()
                    )).toList();
        }

        if (b != null) {
            bookingDto = new BookingDetailsDto(
                    b.getBedId(),
                    b.getRoomId(),
                    b.getFloorId(),
                    b.getRentAmount(),
                    b.getBookingAmount(),
                    b.getCheckoutDate(),
                    b.getRequestedCheckoutDate(),
                    b.getLeavingDate(),
                    b.getNoticeDate(),
                    b.getJoiningDate(),
                    b.getExpectedJoiningDate() != null ? Utils.dateToString(b.getExpectedJoiningDate()) : null,
                    b.getBookingId(),
                    b.getCurrentStatus(),
                    b.getReasonForLeaving(),
                    b.getRoomName(),
                    b.getFloorName(),
                    b.getBedName(),
                    b.getSharingType()
            );
        }

        HostelResponse hostelResponse = null;
        if (hostel != null) {
            hostelResponse = new HostelResponse(hostel.getHostelId(), hostel.getHostelName(),
                    hostel.getMainImage(), hostel.getMobile());
        }

        List<CustomerDocumentsResponse> kycDocuments = new ArrayList<>();
        List<CustomerDocumentsResponse> checkInDocuments = new ArrayList<>();
        List<CustomerDocumentsResponse> otherDocuments = new ArrayList<>();
        if (documents != null && !documents.isEmpty()) {
            for (CustomerDocuments document : documents) {
                CustomerDocumentsResponse response = new CustomerDocumentsResponse(
                        document.getDocumentId(),
                        document.getDocumentType(),
                        document.getDocumentUrl(),
                        document.getDocumentFileType()
                );

                if (DocumentType.KYC.name().equalsIgnoreCase(document.getDocumentType())) {
                    kycDocuments.add(response);
                } else if (DocumentType.CHECKIN.name().equalsIgnoreCase(document.getDocumentType())) {
                    checkInDocuments.add(response);
                } else if (DocumentType.OTHER.name().equalsIgnoreCase(document.getDocumentType())) {
                    otherDocuments.add(response);
                } else {
                    otherDocuments.add(response);
                }
            }
        }

        return new CustomerDetails(
                c.getCustomerId(),
                c.getFirstName(),
                c.getLastName(),
                c.getEmailId(),
                c.getMobile(),
                c.getHouseNo(),
                c.getStreet(),
                c.getLandmark(),
                c.getPincode(),
                c.getCity(),
                c.getState(),
                c.getProfilePic(),
                initials.toString(),
                c.getExpJoiningDate() != null ? Utils.dateToString(c.getExpJoiningDate()) : null,
                c.getCurrentStatus(),
                c.getDateOfBirth(),
                c.getGender(),

                additionalContactsList,

                new CustomerKycDetails(
                        c.getKycDetails() != null ? c.getKycDetails().getCurrentStatus() : null,
                        c.getKycDetails() != null ? c.getKycDetails().getTransactionId() : null,
                        c.getKycDetails() != null ? c.getKycDetails().getReferenceId() : null
                ),

                bookingDto,
                hostelResponse,
                kycDocuments,
                checkInDocuments,
                otherDocuments
        );
    }

    private static StringBuilder getInitials(Customers c) {
        StringBuilder initials = new StringBuilder();
        if (c.getFirstName() != null) {
            initials.append(c.getFirstName().toUpperCase().charAt(0));
        }
        if (c.getLastName() != null && !c.getLastName().trim().equalsIgnoreCase("")) {
            initials.append(c.getLastName().toUpperCase().charAt(0));
        }
        else {
            if (c.getFirstName() != null && c.getFirstName().length() > 1) {
                initials.append(c.getFirstName().toUpperCase().charAt(1));
            }
        }
        return initials;
    }
}

