package com.smartstay.tenant.payload.customer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CustomerAdditionalContactsEditPayload(@NotNull(message = "ContactId is required")
                                                    @Positive(message = "ContactId must be greater than 0")
                                                    Long contactId,
                                                    String name,
                                                    String relationship,
                                                    String occupation,
                                                    String mobile,
                                                    String fullAddress,
                                                    String countryCode) {
}
