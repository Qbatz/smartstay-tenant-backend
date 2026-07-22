package com.smartstay.tenant.controller;

import com.smartstay.tenant.payload.customer.CustomerJobDetailsIdPayload;
import com.smartstay.tenant.payload.customer.CustomerJobDetailsPayload;
import com.smartstay.tenant.service.CustomerJobDetailsService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v2/customer-job-details")
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@SecurityRequirement(name = "Authorization")
@CrossOrigin("*")
public class CustomerJobDetailsController {

    @Autowired
    private CustomerJobDetailsService customerJobDetailsService;

    @PostMapping
    public ResponseEntity<?> createOrUpdateCustomerJobDetails(@RequestBody List<CustomerJobDetailsPayload> payloads) {
        return customerJobDetailsService.createOrUpdateCustomerJobDetails(payloads);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCustomerJobDetails(@RequestBody List<CustomerJobDetailsIdPayload> payloads) {
        return customerJobDetailsService.deleteCustomerDetails(payloads);
    }
}
