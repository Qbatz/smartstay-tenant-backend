package com.smartstay.tenant.controller;

import com.smartstay.tenant.service.HostelService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v2/tenant/hostels")
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@SecurityRequirement(name = "Authorization")
@CrossOrigin("*")
public class DashboardController {

    @Autowired
    private HostelService hostelService;


    @GetMapping("")
    public ResponseEntity<?> getHostels() {
        return hostelService.getHostels();
    }

    @GetMapping("/{hostelId}")
    public ResponseEntity<?> getHostelDetails(@PathVariable String hostelId) {
        return hostelService.getHostelDetails(hostelId);
    }

    @GetMapping("requests/{hostelId}")
    public ResponseEntity<?> getCustomerRequests(@PathVariable("hostelId") String hostelId) {
        return hostelService.getCustomerRequests(hostelId);
    }

    @GetMapping("requests/{hostelId}/{requestId}/{requestType}")
    public ResponseEntity<?> getCustomerRequestById(@PathVariable("hostelId") String hostelId, @PathVariable("requestId") String requestId) {
        return hostelService.getCustomerRequestById(hostelId, requestId);
    }




}
