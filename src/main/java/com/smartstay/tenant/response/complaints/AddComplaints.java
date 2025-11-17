package com.smartstay.tenant.response.complaints;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddComplaints(
        @NotNull(message = "Complaint Type ID cannot be null")
        Integer complaintTypeId,

        Integer floorId,
        Integer roomId,
        Integer bedId,

        String description
) {
}
