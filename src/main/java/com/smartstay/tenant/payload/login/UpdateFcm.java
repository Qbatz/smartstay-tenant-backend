package com.smartstay.tenant.payload.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record UpdateFcm(

        @NotEmpty
        @NotBlank(message = "xuid cannot be blank")
        String xuid,

        @NotEmpty
        @NotBlank(message = "fcmToken cannot be blank")
        String fcmToken

) {
}
