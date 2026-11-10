package com.smartstay.tenant.mapper.amenities;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dao.AmenitiesV1;
import com.smartstay.tenant.dao.CustomersAmenity;
import com.smartstay.tenant.response.amenity.AmenityInfoResponse;

import java.util.Map;
import java.util.function.Function;

public class AmenityInfoResMapper implements Function<AmenitiesV1, AmenityInfoResponse> {

    private final Map<String, CustomersAmenity> customersAmenityMap;

    public AmenityInfoResMapper(Map<String, CustomersAmenity> customersAmenityMap) {
        this.customersAmenityMap = customersAmenityMap;
    }

    @Override
    public AmenityInfoResponse apply(AmenitiesV1 amenity) {

        CustomersAmenity customersAmenity = customersAmenityMap
                .getOrDefault(amenity.getAmenityId(), null);

        double amenityAmount = 0;
        boolean proRate = false;
        String amenityStartDate = null;
        String amenityEndDate = null;

        if (amenity.getAmenityAmount() != null) {
            amenityAmount = amenity.getAmenityAmount();
        }

        if (amenity.getIsProRate() != null) {
            proRate = amenity.getIsProRate();
        }

        if (customersAmenity != null){
            if (customersAmenity.getStartDate() != null) {
                amenityStartDate = Utils.dateToString(customersAmenity.getStartDate());
            }

            if (customersAmenity.getEndDate() != null) {
                amenityEndDate = Utils.dateToString(customersAmenity.getEndDate());
            }
        }

        return new AmenityInfoResponse(amenity.getAmenityId(), proRate, amenityAmount,
                amenity.getAmenityName(), amenityStartDate, amenityEndDate);
    }
}
