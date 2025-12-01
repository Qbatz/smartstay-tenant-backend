package com.smartstay.tenant.response;

public record VerifyOtpResponse(

        String xuid,
        boolean isMpinVerified
) {
}
