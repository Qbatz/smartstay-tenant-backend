package com.smartstay.tenant.payload.login;

import jakarta.validation.constraints.Pattern;

public record UpdateMpin(
        String userId,

        @Pattern(regexp = "\\d{4}", message = "MPIN must be 4 digits")
        String newMpin
) {
}
