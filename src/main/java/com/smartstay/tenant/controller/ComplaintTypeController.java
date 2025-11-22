package com.smartstay.tenant.controller;

import com.smartstay.tenant.service.ComplaintTypeService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v2/ComplaintType")
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@SecurityRequirement(name = "Authorization")
@CrossOrigin("*")
public class ComplaintTypeController {

    @Autowired
    private ComplaintTypeService complaintTypeService;



    @GetMapping("/all-complaintTypes/{hostelId}")
    public ResponseEntity<?> getAllComplaintTypes(@PathVariable("hostelId") String hostelId) {
        return complaintTypeService.getAllComplaintTypes(hostelId);
    }

}
