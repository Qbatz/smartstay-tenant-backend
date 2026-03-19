package com.smartstay.tenant.payload.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UploadDocuments(String notes,
                              @NotBlank(message = "Document type is required")
                              @Pattern(regexp = "^(KYC|CHECKIN|OTHER|kyc|checkin|other)?$", message = "Status must be either 'KYC' or 'CHECKIN' or 'OTHER'")
                              String type) {
}
