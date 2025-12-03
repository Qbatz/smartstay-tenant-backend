package com.smartstay.tenant.controller;

import com.smartstay.tenant.payload.login.Login;
import com.smartstay.tenant.payload.login.TokenLogin;
import com.smartstay.tenant.payload.login.VerifyOtp;
import com.smartstay.tenant.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v2/tenant/user")
@CrossOrigin("*")
public class UserController {

    @Autowired
    UserService userService;


    @PostMapping("/verify-mobile")
    public ResponseEntity<?> login(@Valid @RequestBody Login login) {
        return userService.login(login);
    }

    @PostMapping("/resend-otp/{xuid}")
    public ResponseEntity<?> resendOtp(@PathVariable String xuid) {
        return userService.resendOtp(xuid);
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtp verifyOtp) {
        return userService.verifyOtp(verifyOtp);
    }



}
