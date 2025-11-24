package com.smartstay.tenant.payload.bedChange;

public record BedChangePayload(
        String title,
        String description,
        Integer startFrom,
        Integer bedId,
        Integer roomId,
        Integer floorId,
        String preferredType,
        String reason
) {
}
