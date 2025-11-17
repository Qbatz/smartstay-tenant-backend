package com.smartstay.tenant.controller;


import com.smartstay.tenant.payload.complaint.AddComplaintComment;
import com.smartstay.tenant.response.complaints.AddComplaints;
import com.smartstay.tenant.service.ComplaintService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("v2/tenant/complaints")
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@SecurityRequirement(name = "Authorization")
@CrossOrigin("*")
public class ComplaintsController {

    @Autowired
    private ComplaintService complaintsService;


    @GetMapping("/{hostelId}")
    public ResponseEntity<?> getAllComplaints(@PathVariable String hostelId,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        return complaintsService.getComplaintList(hostelId, page, size);
    }

    @GetMapping("/{hostelId}/{complaintId}")
    public ResponseEntity<?> getComplaintById(
            @PathVariable String hostelId,
            @PathVariable Integer complaintId

    ) {
        return complaintsService.getComplaintById(hostelId, complaintId);
    }


    @PostMapping("/{hostelId}")
    public ResponseEntity<?> addComplaint(@RequestPart(required = false, name = "complaintImage") List<MultipartFile> complaintImages,  @RequestPart(value = "payloads", required = false) AddComplaints payloads, @PathVariable("hostelId") String hostelId) {
        return complaintsService.addComplaint(complaintImages, payloads, hostelId);
    }

    @DeleteMapping("/delete-complaint/{complaintId}/{hostelId}")
    public ResponseEntity<?> deleteComplaint(@PathVariable("complaintId") Integer complaintId,@PathVariable String hostelId) {
        return complaintsService.deleteComplaint(complaintId,hostelId);
    }

    @PostMapping("/add-comment/{complaintId}")
    public ResponseEntity<?> addComplaintComments(@PathVariable("complaintId") int complaintId,@Valid @RequestBody AddComplaintComment comment) {
        return complaintsService.addComplaintComments(comment,complaintId);
    }

    @DeleteMapping("/complaint-image/{complaintId}/{imageId}/{hostelId}")
    public ResponseEntity<?> deactivateComplaintImage(
            @PathVariable Integer complaintId,
            @PathVariable Integer imageId,
            @PathVariable String hostelId
    ) {
        return complaintsService.deactivateComplaintImage(complaintId, imageId, hostelId);
    }


}
