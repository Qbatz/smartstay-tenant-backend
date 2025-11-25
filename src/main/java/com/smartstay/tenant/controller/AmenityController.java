package com.smartstay.tenant.controller;

import com.smartstay.tenant.payload.amenity.RequestAmenity;
import com.smartstay.tenant.payload.notification.NotificationRequest;
import com.smartstay.tenant.service.AmenitiesService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v2/amenities")
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@SecurityRequirement(name = "Authorization")
@CrossOrigin("*")
public class AmenityController {

    @Autowired
    private AmenitiesService amenitiesService;

    @GetMapping("/{hostelId}")
    public ResponseEntity<?> getAllAmenities(@PathVariable("hostelId") String hostelId) {
        return amenitiesService.getAllAmenities(hostelId);
    }

    @GetMapping("/{hostelId}/{amenityId}")
    public ResponseEntity<?> getAmenityById(
            @PathVariable("hostelId") String hostelId,
            @PathVariable("amenityId") String amenityId
    ) {
        return amenitiesService.getAmenityByAmenityId(hostelId, amenityId);
    }

    @PostMapping("/request-amenity/{hostelId}/{amenityId}")
    public ResponseEntity<?> createRequest(@PathVariable("hostelId") String hostelId,@PathVariable("amenityId") String amenityId, @Valid @RequestBody RequestAmenity amenityRequest) {
        return amenitiesService.createAmenityRequest(hostelId,amenityId, amenityRequest);
    }

    @GetMapping("all-requests/{hostelId}")
    public ResponseEntity<?> getAmenityRequest(@PathVariable("hostelId") String hostelId) {
        return amenitiesService.getAmenityRequest(hostelId);
    }




}
