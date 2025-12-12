package com.smartstay.tenant.controller;


import com.smartstay.tenant.payload.login.UpdateFcm;
import com.smartstay.tenant.service.ConfigService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v2/config")
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@SecurityRequirement(name = "Authorization")
@CrossOrigin("*")
public class ConfigController {

    @Autowired
    ConfigService configService;


    @PostMapping("/update-fcm")
    public ResponseEntity<?> updateFcm(@Valid @RequestBody UpdateFcm updateFcm) {
        return configService.updateFcm(updateFcm);
    }
}
