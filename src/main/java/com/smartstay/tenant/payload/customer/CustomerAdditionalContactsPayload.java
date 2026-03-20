package com.smartstay.tenant.payload.customer;

import jakarta.validation.constraints.NotBlank;

public record CustomerAdditionalContactsPayload(@NotBlank(message = "Name is required")
                                                String name,
                                                String relationship,
                                                String occupation,
                                                @NotBlank(message = "Mobile is required")
                                                String mobile,
                                                String fullAddress,
                                                String countryCode) {
}
