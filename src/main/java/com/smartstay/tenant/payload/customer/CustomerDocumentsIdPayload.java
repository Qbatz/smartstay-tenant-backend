package com.smartstay.tenant.payload.customer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CustomerDocumentsIdPayload(@NotNull(message = "DocumentId is required")
                                         @Positive(message = "DocumentId must be greater than 0")
                                         Long documentId) {
}
