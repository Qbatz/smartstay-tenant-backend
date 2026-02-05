package com.smartstay.tenant.response.amenity;

public interface AmenityDetails {

    String getAmenityId();
    String getAmenityName();
    Double getAmenityAmount();
    String getDescription();
    String getTermsAndCondition();
    Boolean getProRate();
    Boolean getIsAssigned();
}
