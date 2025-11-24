package com.smartstay.tenant.controller;


import com.smartstay.tenant.payload.bedChange.BedChangePayload;
import com.smartstay.tenant.payload.notification.NotificationRequest;
import com.smartstay.tenant.service.BedsService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v2/bed")
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@SecurityRequirement(name = "Authorization")
@CrossOrigin("*")
public class BedController {

    @Autowired
    private BedsService bedsService;

    @PostMapping("/request-bedChange/{hostelId}")
    public ResponseEntity<?> createRequest(@PathVariable("hostelId") String hostelId, @Valid @RequestBody BedChangePayload request) {
        return bedsService.requestBedChange(hostelId, request);
    }
}
