package com.smartstay.tenant.payload.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Login(

        @NotBlank(message = "Mobile number is required")
        @Size(min = 10, max = 10, message = "Invalid MobileNumber")
        String mobile

) {
}
