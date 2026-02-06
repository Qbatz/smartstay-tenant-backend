package com.smartstay.tenant.mapper;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.dto.BookingDetailsDto;
import com.smartstay.tenant.response.customer.CustomerDetails;
import com.smartstay.tenant.response.customer.CustomerKycDetails;
import com.smartstay.tenant.response.customer.CustomersBookingDetails;


public class CustomerMapper {

    public CustomerDetails toDetailsDto(Customers c, CustomersBookingDetails b) {

        BookingDetailsDto bookingDto = null;

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
                    b.getBedName()
            );
        }

        return new CustomerDetails(
                c.getCustomerId(),
                c.getFirstName(),
                c.getLastName(),
                c.getHouseNo(),
                c.getStreet(),
                c.getLandmark(),
                c.getPincode(),
                c.getCity(),
                c.getState(),
                c.getProfilePic(),
                initials.toString(),
                c.getExpJoiningDate() != null ? Utils.dateToString(c.getExpJoiningDate()) : null,
                c.getDateOfBirth(),
                c.getGender(),

                new CustomerKycDetails(
                        c.getKycDetails() != null ? c.getKycDetails().getCurrentStatus() : null,
                        c.getKycDetails() != null ? c.getKycDetails().getTransactionId() : null,
                        c.getKycDetails() != null ? c.getKycDetails().getReferenceId() : null
                ),

                bookingDto
        );
    }
}

