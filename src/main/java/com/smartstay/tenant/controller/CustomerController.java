package com.smartstay.tenant.controller;

import com.smartstay.tenant.payload.login.Login;
import com.smartstay.tenant.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v2/customers")
@CrossOrigin("*")
public class CustomerController {

    @Autowired
    CustomerService customerService;


    @GetMapping("/customer-details")
    public ResponseEntity<?> customerDetails() {
        return customerService.getCustomerDetails();
    }


}
