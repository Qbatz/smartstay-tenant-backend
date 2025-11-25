package com.smartstay.tenant.response.amenity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmenityRequestResponse {
    private Long requestId;
    private String hostelId;
    private String customerId;
    private String amenityId;
    private String amenityName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/mm/yyyy", timezone = "UTC")
    private Date requestedDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/mm/yyyy", timezone = "UTC")
    private Date startFrom;
    private String status;
    private String description;
}

