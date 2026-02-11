package com.smartstay.tenant.response.amenity;

import java.util.Date;

public interface AmenityDetails {

    String getAmenityId();
    String getAmenityName();
    Double getAmenityAmount();
    String getDescription();
    String getTermsAndCondition();
    Boolean getProRate();
    String getHostelId();
    Boolean getIsAssigned();
    Date getAmenityStartDate();
}
