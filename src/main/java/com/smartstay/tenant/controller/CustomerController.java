package com.smartstay.tenant.controller;

import com.smartstay.tenant.response.customer.EditCustomer;
import com.smartstay.tenant.service.CustomerService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("v2/tenants")
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@SecurityRequirement(name = "Authorization")
@CrossOrigin("*")
public class CustomerController {

    @Autowired
    CustomerService customerService;


    @GetMapping("/details")
    public ResponseEntity<?> customerDetails() {
        return customerService.getCustomerDetails();
    }

    @PostMapping("/update-profile")
    public ResponseEntity<?> updateCustomer(@Valid @RequestPart(value = "payloads") EditCustomer customerInfo, @RequestPart(value = "profilePic", required = false) MultipartFile file) {
        return customerService.updateCustomerInfo(customerInfo, file);
    }


}
