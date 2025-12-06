package com.smartstay.tenant.payload.complaint;

import jakarta.validation.constraints.NotNull;

public record UpdateComplaint(
        Integer complaintTypeId,

        Integer floorId,
        Integer roomId,
        Integer bedId,
        String description,

        Boolean isActive
) {
}
