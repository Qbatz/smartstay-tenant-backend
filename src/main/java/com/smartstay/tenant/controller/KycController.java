package com.smartstay.tenant.controller;

import com.smartstay.tenant.service.KycService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v2/kyc")
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@SecurityRequirement(name = "Authorization")
@CrossOrigin("*")
public class KycController {

    @Autowired
    private KycService kycService;

    @GetMapping("/verify")
    public ResponseEntity<?> verifyKycStatus() {
       return kycService.verifyKycStatus();
    }

    @PostMapping("/status")
    public ResponseEntity<?> updateStatusToWaitingApproval() {
        return kycService.updateStatusToWaitingApproval();
    }
}
