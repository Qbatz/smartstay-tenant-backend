package com.smartstay.tenant.payload.amenity;

public record RequestAmenity(
        String title,
        String description,
        String startFrom
) {
}
