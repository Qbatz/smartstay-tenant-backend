package com.smartstay.tenant.mapper.bed;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dao.BedChangeRequest;
import com.smartstay.tenant.ennum.RequestStatus;
import com.smartstay.tenant.ennum.RequestType;
import com.smartstay.tenant.repository.BedsRepository;
import com.smartstay.tenant.response.hostel.RequestItemResponse;

import java.util.function.Function;

public class BedChangeRequestMapper implements Function<BedChangeRequest, RequestItemResponse> {

    private BedsRepository bedsRepository;

    public BedChangeRequestMapper(BedsRepository bedsRepository) {
        this.bedsRepository = bedsRepository;
    }

    @Override
    public RequestItemResponse apply(BedChangeRequest bedChangeRequest) {
        String status = "";
        int statusCode = 0;
        String requestedItem = null;

        if (bedChangeRequest.getCurrentStatus().equalsIgnoreCase(RequestStatus.OPEN.name())) {
            status = "Open";
            statusCode = 1;
        }
        else if (bedChangeRequest.getCurrentStatus().equalsIgnoreCase(RequestStatus.PENDING.name())) {
            status = "Pending";
            statusCode = 2;
        }
        else if (bedChangeRequest.getCurrentStatus().equalsIgnoreCase(RequestStatus.INPROGRESS.name())) {
            status = "In Progress";
            statusCode = 3;
        }
        else if (bedChangeRequest.getCurrentStatus().equalsIgnoreCase(RequestStatus.ONHOLD.name())) {
            status = "On Hold";
            statusCode = 4;
        }
        else if (bedChangeRequest.getCurrentStatus().equalsIgnoreCase(RequestStatus.CLOSED.name())) {
            status = "Closed";
            statusCode = 5;
        }
        else if (bedChangeRequest.getCurrentStatus().equalsIgnoreCase(RequestStatus.REJECTED.name())) {
            status = "Rejected";
            statusCode = 6;
        }
        else {
            status = "Unknown";
            statusCode = -1;
        }

        if (bedChangeRequest.getBedId() != null) {
            var bed = bedsRepository.findByBedId(bedChangeRequest.getBedId());
            if (bed != null) {
                requestedItem = bed.getBedName();
            }
        }


        return new RequestItemResponse(String.valueOf(bedChangeRequest.getId()),
                Utils.capitalize(RequestType.CHANGE_BED.name().replace("_", " ")),
                Utils.dateToString(bedChangeRequest.getCreatedAt()),
                status,
                "Requested bed change",
                bedChangeRequest.getReason(),
                requestedItem,
                statusCode);
    }
}
