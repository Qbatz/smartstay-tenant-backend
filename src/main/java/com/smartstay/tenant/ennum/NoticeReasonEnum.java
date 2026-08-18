package com.smartstay.tenant.ennum;

import lombok.Getter;

@Getter
public enum NoticeReasonEnum {

    JOB_SWITCH("Job switch"),
    REASON_NOT_GIVEN("Reason not given");

    private final String value;

    NoticeReasonEnum(String value) {
        this.value = value;
    }
}
