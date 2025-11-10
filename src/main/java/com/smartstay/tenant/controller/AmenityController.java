package com.smartstay.tenant.controller;

import com.smartstay.tenant.service.AmenitiesService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("tenant/amenity")
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@SecurityRequirement(name = "Authorization")
@CrossOrigin("*")
public class AmenityController {

    @Autowired
    private AmenitiesService amenitiesService;

    @GetMapping("/assigned-amenities/{hostelId}")
    public ResponseEntity<?> getAllAssignedAmenities(@PathVariable("hostelId") String hostelId) {
        return amenitiesService.getAllAssignedAmenities(hostelId);
    }

    @GetMapping("/unassigned-amenities/{hostelId}")
    public ResponseEntity<?> getAllUnAssignedAmenities(@PathVariable("hostelId") String hostelId) {
        return amenitiesService.getAllUnAssignedAmenities(hostelId);
    }


}
