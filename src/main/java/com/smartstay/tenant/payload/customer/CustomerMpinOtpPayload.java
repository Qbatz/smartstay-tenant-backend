package com.smartstay.tenant.payload.customer;

import jakarta.validation.constraints.*;

public record CustomerMpinOtpPayload(@NotBlank(message = "MPIN is required")
                                     @Pattern(regexp = "^\\d{4}$", message = "MPIN must be exactly 4 digits")
                                     String mpin,
                                     @NotBlank(message = "OTP is required")
                                     @Pattern(regexp = "^\\d{6}$", message = "OTP must be exactly 6 digits")
                                     String otp) {
}
