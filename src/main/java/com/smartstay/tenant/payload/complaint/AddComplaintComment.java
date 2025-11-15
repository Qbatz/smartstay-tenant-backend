package com.smartstay.tenant.payload.complaint;

import jakarta.validation.constraints.NotBlank;

public record AddComplaintComment(
        @NotBlank(message = "Message is required")
        String message,

        @NotBlank(message = "HostelId is required")
        String hostelId

        ) {
}
