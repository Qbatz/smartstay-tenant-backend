package com.smartstay.tenant.response;

public record VerifyOtpResponse(

        String userId,
        boolean isMpinVerified
) {
}
