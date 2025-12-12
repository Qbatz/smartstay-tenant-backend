package com.smartstay.tenant.response.login;

public record VerifyMobileResponse(
        String xuid,
        Integer otp
) {
}
