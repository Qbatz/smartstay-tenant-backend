package com.smartstay.tenant.response.hostel;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestItemResponse {

    private Long requestId;
    private String type;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/mm/yyyy", timezone = "UTC")
    private Date requestedDate;
    private String status;
    private String title;
    private String description;
    private String requestedItem;
}

