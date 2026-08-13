package com.smartstay.tenant.ennum;

import lombok.Getter;

@Getter
public enum BedChangeUrgencyEnum {

    WITHIN_2_TO_3_DAYS("Within 2-3 days"),
    WITHIN_1_WEEK("Within 1 week"),
    NEXT_MONTH_START("Next month start"),;

    private final String value;

    BedChangeUrgencyEnum(String value) {
        this.value = value;
    }
}
