package com.smartstay.tenant.dto.invoice;

public record BedHistory(String bedName,
                         String roomName,
                         String floorName,
                         long noOfDaysStayed,
                         Double rent) {
}
