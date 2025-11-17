package com.smartstay.tenant.response;

public record VerifyOtpResponse(

        String kycStatus,
        String accessToken
) {
}
