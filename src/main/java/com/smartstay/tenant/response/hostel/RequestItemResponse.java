package com.smartstay.tenant.response.hostel;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestItemResponse implements Comparable<RequestItemResponse> {
    private String requestId;
    private String type;
    private String requestedDate;
    private String requestedTime;
    private String requestedDateDisplay;
    private String status;
    private String title;
    private String description;
    private String requestedItem;
    private int statusCode;

    @Override
    public int compareTo(RequestItemResponse o) {
        return this.requestedDate.compareTo(o.requestedDate);
    }
}

