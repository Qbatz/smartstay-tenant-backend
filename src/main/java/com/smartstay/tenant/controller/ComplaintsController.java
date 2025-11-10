package com.smartstay.tenant.controller;


import com.smartstay.tenant.response.complaints.AddComplaints;
import com.smartstay.tenant.service.ComplaintService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("v2/tenant/complaints")
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@SecurityRequirement(name = "Authorization")
@CrossOrigin("*")
public class ComplaintsController {

    @Autowired
    private ComplaintService complaintsService;


    @GetMapping("/complaint-list/{hostelId}")
    public ResponseEntity<?> getAllComplaints(@PathVariable String hostelId) {
        return complaintsService.getComplaintList(hostelId);
    }

    @PostMapping("/add-complaint")
    public ResponseEntity<?> addComplaint(@RequestPart(required = false, name = "complaintImage") MultipartFile mainImage, @RequestPart AddComplaints payloads) {
        return complaintsService.addComplaint(mainImage, payloads);
    }
}
