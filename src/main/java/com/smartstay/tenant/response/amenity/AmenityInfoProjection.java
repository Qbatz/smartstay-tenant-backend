package com.smartstay.tenant.response.amenity;

import java.util.Date;

public interface AmenityInfoProjection {

    String getAmenityId();
    String getAmenityName();
    Double getAmenityAmount();
    Boolean getProRate();
    Date getAmenityStartDate();
    Date getAmenityEndDate();
}
