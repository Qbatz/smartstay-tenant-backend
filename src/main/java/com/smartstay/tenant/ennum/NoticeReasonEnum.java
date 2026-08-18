package com.smartstay.tenant.ennum;

import lombok.Getter;

@Getter
public enum NoticeReasonEnum {

    JOB_RELOCATION("Job / Workplace Relocation"),
    RETURNING_TO_HOMETOWN("Returning to Hometown"),
    COLLEGE_COURSE_COMPLETED("College / Course Completed"),
    LOOKING_FOR_ANOTHER_ACCOMMODATION("Looking for Another PG / Accommodation"),
    MOVING_WITH_FAMILY("Moving with Family"),
    PERSONAL_OTHER_REASONS("Personal / Other Reasons");

    private final String value;

    NoticeReasonEnum(String value) {
        this.value = value;
    }
}
