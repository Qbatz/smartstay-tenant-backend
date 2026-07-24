package com.smartstay.tenant.payload.customer;

import jakarta.validation.constraints.NotNull;

public record CustomerJobDetailsIdPayload(@NotNull(message = "JobId is required")
                                          Long jobId) {
}
