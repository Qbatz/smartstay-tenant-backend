package com.smartstay.tenant.payload.customer;

import jakarta.validation.constraints.*;

public record CustomerMpinPayload(@NotBlank(message = "MPIN is required")
                                  @Pattern(regexp = "^\\d{4}$", message = "MPIN must be exactly 4 digits")
                                  String mpin) {
}
