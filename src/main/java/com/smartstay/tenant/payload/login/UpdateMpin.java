package com.smartstay.tenant.payload.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateMpin(@NotBlank(message = "xuid cannot be blank")
                         String xuid,
                         String mobileNo,
                         @Pattern(regexp = "\\d{4}", message = "MPIN must be 4 digits")
                         String mPin,
                         String platform) {
}
