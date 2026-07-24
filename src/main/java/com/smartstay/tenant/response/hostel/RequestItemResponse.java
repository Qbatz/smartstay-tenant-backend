package com.smartstay.tenant.response.hostel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestItemResponse implements Comparable<RequestItemResponse> {
    private String requestId;
    private String type;
    private String requestType;
    @JsonIgnore
    private Date dbRequestedDate;
    private String requestedDate;
    private String requestedTime;
    private String requestedDateDisplay;
    private String status;
    private String title;
    private String description;
    private String requestedItem;
    private int statusCode;
    private String reason;
    private double amenityPrice;
    private boolean amenityProRate;
    private String preferredBedType;
    private String bedChangeStartsFrom;

    @Override
    public int compareTo(RequestItemResponse o) {
        return this.requestedDate.compareTo(o.requestedDate);
    }
}

