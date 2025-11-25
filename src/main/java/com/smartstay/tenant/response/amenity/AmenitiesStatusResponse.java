package com.smartstay.tenant.response.amenity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmenitiesStatusResponse {
    private List<AmenityInfoProjection> assignedAmenities;
    private List<AmenityInfoProjection> unassignedAmenities;
}

