package com.smartstay.tenant.controller;

import com.smartstay.tenant.payload.login.TokenLogin;
import com.smartstay.tenant.payload.login.UpdateMpin;
import com.smartstay.tenant.service.LoginService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v2/tenant/login")
@CrossOrigin("*")
public class LoginController {

    @Autowired
    LoginService loginService;


    @PostMapping("/set-Mpin")
    public ResponseEntity<?> setMpin(@Valid @RequestBody UpdateMpin updateMpin) {
        return loginService.updateMpin(updateMpin);
    }

    @PostMapping("/verify-Mpin")
    public ResponseEntity<?> verifyMpin(@Valid @RequestBody UpdateMpin updateMpin) {
        return loginService.verifyMpin(updateMpin);
    }


}
