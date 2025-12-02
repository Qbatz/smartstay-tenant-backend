package com.smartstay.tenant.payload.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record RequestToken(

        @NotEmpty
        @NotBlank(message = "xuid cannot be blank")
        String xuid,

        @NotBlank(message = "HostelId cannot be blank")
        @NotEmpty
        String hostelId

) {
}
