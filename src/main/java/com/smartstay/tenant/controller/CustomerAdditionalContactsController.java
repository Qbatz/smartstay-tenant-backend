package com.smartstay.tenant.controller;

import com.smartstay.tenant.payload.customer.CustomerAdditionalContactsIdPayload;
import com.smartstay.tenant.payload.customer.CustomerAdditionalContactsPayload;
import com.smartstay.tenant.service.CustomerAdditionalContactsService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v2/customer-additional-contacts")
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@SecurityRequirement(name = "Authorization")
@CrossOrigin("*")
public class CustomerAdditionalContactsController {

    @Autowired
    private CustomerAdditionalContactsService customerAdditionalContactsService;

    @PostMapping
    public ResponseEntity<?> addCustomerAdditionalContacts(@RequestBody List<CustomerAdditionalContactsPayload> payloads) {
        return customerAdditionalContactsService.addAdditionalContacts(payloads);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCustomerAdditionalContacts(@RequestBody List<CustomerAdditionalContactsIdPayload> payloads){
        return customerAdditionalContactsService.deleteAdditionalContacts(payloads);
    }
}
