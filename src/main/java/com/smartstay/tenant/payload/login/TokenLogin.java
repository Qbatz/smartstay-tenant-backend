package com.smartstay.tenant.payload.login;

import jakarta.validation.constraints.NotBlank;

public record TokenLogin(
        @NotBlank
        String mobileNo,

        @NotBlank
        String serialNo
) {
}
