package com.smartstay.tenant.controller;

import com.smartstay.tenant.payload.login.Login;
import com.smartstay.tenant.payload.login.VerifyOtp;
import com.smartstay.tenant.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v2/customers-users")
@CrossOrigin("*")
public class UserController {

    @Autowired
    CustomerService customerService;



    @PostMapping("/verify-mobile")
    public ResponseEntity<?> login(@Valid @RequestBody Login login) {
        return customerService.login(login);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtp verifyOtp) {
        return customerService.verifyOtp(verifyOtp);
    }



}
