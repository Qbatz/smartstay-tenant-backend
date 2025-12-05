package com.smartstay.tenant.mapper.amenities;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dao.AmenitiesV1;
import com.smartstay.tenant.dao.AmenityRequest;
import com.smartstay.tenant.ennum.RequestStatus;
import com.smartstay.tenant.ennum.RequestType;
import com.smartstay.tenant.response.hostel.RequestItemResponse;

import java.util.List;
import java.util.function.Function;

public class AmenityRequestMapper implements Function<AmenityRequest, RequestItemResponse> {

    private List<AmenitiesV1> listAmenities = null;

    public AmenityRequestMapper(List<AmenitiesV1> listAmenities) {
        this.listAmenities = listAmenities;
    }

    @Override
    public RequestItemResponse apply(AmenityRequest amenityRequest) {
        String status = "";
        int statusCode = 0;
        String amenityName = null;

        if (amenityRequest.getCurrentStatus().equalsIgnoreCase(RequestStatus.OPEN.name())) {
            status = "Open";
            statusCode = 1;
        }
        else if (amenityRequest.getCurrentStatus().equalsIgnoreCase(RequestStatus.PENDING.name())) {
            status = "Pending";
            statusCode = 2;
        }
        else if (amenityRequest.getCurrentStatus().equalsIgnoreCase(RequestStatus.INPROGRESS.name())) {
            status = "In Progress";
            statusCode = 3;
        }
        else if (amenityRequest.getCurrentStatus().equalsIgnoreCase(RequestStatus.ONHOLD.name())) {
            status = "On Hold";
            statusCode = 4;
        }
        else if (amenityRequest.getCurrentStatus().equalsIgnoreCase(RequestStatus.CLOSED.name())) {
            status = "Closed";
            statusCode = 5;
        }
        else if (amenityRequest.getCurrentStatus().equalsIgnoreCase(RequestStatus.REJECTED.name())) {
            status = "Rejected";
            statusCode = 6;
        }
        else {
            status = "Unknown";
            statusCode = 7;
        }


        if (listAmenities != null) {
            AmenitiesV1 amenitiesV1 = listAmenities
                    .stream()
                    .filter(i -> i.getAmenityId().equalsIgnoreCase(amenityRequest.getAmenityId()))
                    .findFirst()
                    .orElse(null);

            if (amenitiesV1 != null) {
                amenityName = amenitiesV1.getAmenityName();
            }
        }


        return new RequestItemResponse(amenityRequest.getAmenityRequestId(),
                RequestType.AMENITY_REQUEST.name(),
                Utils.dateToString(amenityRequest.getRequestedDate()),
                status,
                "Requested " + amenityName,
                amenityRequest.getDescription(),
                amenityRequest.getAmenityId(),
                statusCode);
    }
}
