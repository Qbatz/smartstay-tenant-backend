package com.smartstay.tenant.controller;

import com.smartstay.tenant.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v2/customers")
@CrossOrigin("*")
public class CustomerController {

    @Autowired
    CustomerService customerService;

}
