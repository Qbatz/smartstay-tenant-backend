package com.smartstay.tenant.dto.kyc;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DigioKycDetails(@JsonProperty("aadhaar")
                              DigioKycAadhaarDetails aadhaarDetails) {
}
