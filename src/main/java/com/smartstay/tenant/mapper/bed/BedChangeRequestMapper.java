package com.smartstay.tenant.mapper.bed;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dao.BedChangeRequest;
import com.smartstay.tenant.ennum.RequestType;
import com.smartstay.tenant.response.hostel.RequestItemResponse;

import java.util.function.Function;

public class BedChangeRequestMapper implements Function<BedChangeRequest, RequestItemResponse> {
    @Override
    public RequestItemResponse apply(BedChangeRequest bedChangeRequest) {
        String status = null;
        int statusCode = 0;

        return new RequestItemResponse(bedChangeRequest.getId(),
                RequestType.CHANGE_BED.name(),
                Utils.dateToString(bedChangeRequest.getCreatedAt()),
                status,
                "Requested bed change",
                bedChangeRequest.getReason(),
                null,
                statusCode);
    }
}
