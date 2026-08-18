package com.smartstay.tenant.payload.customer;

import jakarta.validation.constraints.NotBlank;

public record RaiseNoticePayload(@NotBlank(message = "Reason is required")
                                 String reason,
                                 String remarks) {
}
