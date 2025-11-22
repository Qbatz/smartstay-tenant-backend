package com.smartstay.tenant.ennum;

public enum RequestStatus {

    PENDING("Pending"),
    OPEN("Open"),
    ONHOLD("Hold"),
    REJECTED("Rejected"),
    CLOSED("Closed"),
    INPROGRESS("In-Progress");

    private final String status;
    RequestStatus(String status) {
        this.status = status;
    }
}
