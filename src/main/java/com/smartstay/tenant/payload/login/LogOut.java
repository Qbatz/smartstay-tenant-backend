package com.smartstay.tenant.payload.login;

import jakarta.validation.constraints.NotBlank;

public record LogOut(
        @NotBlank(message = "xuid cannot be blank")
        String xuid
) {
}
