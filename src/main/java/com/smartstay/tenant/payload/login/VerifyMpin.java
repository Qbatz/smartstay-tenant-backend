package com.smartstay.tenant.payload.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyMpin(
        @NotBlank(message = "xuid cannot be blank")
        String xuid,

        @Pattern(regexp = "\\d{4}", message = "MPIN must be 4 digits")
        String mPin
) {
}
