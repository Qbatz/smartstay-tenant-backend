package com.smartstay.tenant.response.amenity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmenityRequestResponse {
    private Long requestId;
    private String hostelId;
    private String customerId;
    private String amenityId;
    private String amenityName;
    private String requestedDate;
    private String startFrom;
    private String status;
    private String description;
}

