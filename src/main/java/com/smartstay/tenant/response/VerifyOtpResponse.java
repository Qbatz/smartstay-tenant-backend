package com.smartstay.tenant.response;

public record VerifyOtpResponse(

        String customerId,
        String accessToken
) {
}
