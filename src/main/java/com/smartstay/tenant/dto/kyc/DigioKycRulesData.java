package com.smartstay.tenant.dto.kyc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record DigioKycRulesData(@JsonProperty("approval_rule")
                                List<Object> approvalRule) {
}
