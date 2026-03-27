package com.smartstay.tenant.response.eb;

public record EbReadingsResponse(String floorName,
                                 String roomName,
                                 String bedName,
                                 String fromDate,
                                 String toDate
                                 ) {
}
