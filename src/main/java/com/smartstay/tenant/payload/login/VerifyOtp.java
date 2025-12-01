package com.smartstay.tenant.payload.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record VerifyOtp(
        @NotBlank
        String mobileNo,

        @NotNull
        @NotEmpty
        Integer otp
) {
}
