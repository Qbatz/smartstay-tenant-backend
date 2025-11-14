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

import java.util.List;

@RestController
@RequestMapping("v2/complaints")
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@SecurityRequirement(name = "Authorization")
@CrossOrigin("*")
public class ComplaintsController {

    @Autowired
    private ComplaintService complaintsService;


    @GetMapping("/{hostelId}")
    public ResponseEntity<?> getAllComplaints(@PathVariable String hostelId) {
        return complaintsService.getComplaintList(hostelId);
    }

    @GetMapping("/{hostelId}/{complaintId}")
    public ResponseEntity<?> getComplaintById(
            @PathVariable String hostelId,
            @PathVariable Integer complaintId
    ) {
        return complaintsService.getComplaintById(hostelId, complaintId);
    }


    @PostMapping("/{hostelId}")
    public ResponseEntity<?> addComplaint(@RequestPart(required = false, name = "complaintImage") List<MultipartFile> complaintImages, @RequestPart AddComplaints payloads, @PathVariable("hostelId") String hostelId) {
        return complaintsService.addComplaint(complaintImages, payloads, hostelId);
    }
}
