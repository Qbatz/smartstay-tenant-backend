package com.smartstay.tenant.mapper.amenities;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.response.amenity.AmenityInfoProjection;
import com.smartstay.tenant.response.amenity.AmenityInfoResponse;

import java.util.function.Function;

public class AmenityInfoResMapper implements Function<AmenityInfoProjection, AmenityInfoResponse> {

    @Override
    public AmenityInfoResponse apply(AmenityInfoProjection amenityInfoProjection) {

        double amenityAmount = 0;
        boolean proRate = false;
        String amenityStartDate = null;
        String amenityEndDate = null;

        if (amenityInfoProjection.getAmenityAmount() != null) {
            amenityAmount = amenityInfoProjection.getAmenityAmount();
        }

        if (amenityInfoProjection.getProRate() != null) {
            proRate = amenityInfoProjection.getProRate();
        }

        if (amenityInfoProjection.getAmenityStartDate() != null) {
            amenityStartDate = Utils.dateToString(amenityInfoProjection.getAmenityStartDate());
        }

        if (amenityInfoProjection.getAmenityEndDate() != null) {
            amenityEndDate = Utils.dateToString(amenityInfoProjection.getAmenityEndDate());
        }

        return new AmenityInfoResponse(amenityInfoProjection.getAmenityId(), proRate, amenityAmount,
                amenityInfoProjection.getAmenityName(), amenityStartDate, amenityEndDate);
    }
}
