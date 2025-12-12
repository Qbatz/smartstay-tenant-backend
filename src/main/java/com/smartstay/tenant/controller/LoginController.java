package com.smartstay.tenant.controller;

import com.smartstay.tenant.payload.login.*;
import com.smartstay.tenant.service.LoginService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v2/tenant/login")
@CrossOrigin("*")
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@SecurityRequirement(name = "Authorization")
public class LoginController {

    @Autowired
    LoginService loginService;


    @PostMapping("/request-token")
    public ResponseEntity<?> requestToken(@Valid @RequestBody RequestToken requestToken) {
        return loginService.requestToken(requestToken);
    }

    @PostMapping("/set-Mpin")
    public ResponseEntity<?> setMpin(@Valid @RequestBody UpdateMpin updateMpin) {
        return loginService.updateMpin(updateMpin);
    }

    @PostMapping("/verify-Mpin")
    public ResponseEntity<?> verifyMpin(@Valid @RequestBody VerifyMpin verifyMpin) {
        return loginService.verifyMpin(verifyMpin);
    }

    @PostMapping("/log-out")
    public ResponseEntity<?> logout(@Valid @RequestBody LogOut logOut) {
        return loginService.logOut(logOut);
    }

    @PostMapping("/hostels/{xuid}")
    public ResponseEntity<?> getHostels(@PathVariable String xuid) {
        return loginService.getHostelsList(xuid);
    }

    @GetMapping("/hostels-list/{xuid}")
    public ResponseEntity<?> getHostelsWithOutToken(@PathVariable String xuid) {
        return loginService.getHostelsListWithToken(xuid);
    }


}
