package com.smartstay.tenant.response.customer;

import java.util.Date;

public interface CustomersBookingDetails {
    Integer getBedId();
    Integer getRoomId();
    Integer getFloorId();
    Double getRentAmount();
    Double getBookingAmount();

    Date getCheckoutDate();
    Date getRequestedCheckoutDate();
    Date getLeavingDate();
    Date getNoticeDate();
    Date getJoiningDate();
    Date getExpectedJoiningDate();

    String getBookingId();
    String getCurrentStatus();
    String getReasonForLeaving();

    String getRoomName();
    String getFloorName();
    String getBedName();
}
