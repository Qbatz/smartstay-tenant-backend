package com.smartstay.tenant.payload.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RaiseNoticePayload(@NotNull(message = "Request date is required")
                                 @JsonFormat(pattern = "dd-MM-yyyy")
                                 LocalDate requestDate,
                                 @NotNull(message = "Checkout date is required")
                                 @JsonFormat(pattern = "dd-MM-yyyy")
                                 LocalDate checkoutDate,
                                 String reason) {
}
