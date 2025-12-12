package com.smartstay.tenant.controller;


import com.smartstay.tenant.payload.complaint.AddComplaintComment;
import com.smartstay.tenant.payload.complaint.DeleteComplaintRequest;
import com.smartstay.tenant.payload.complaint.UpdateComplaint;
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
@RequestMapping("v2/complaints")
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

    @PutMapping("/{hostelId}/{complaintId}")
    public ResponseEntity<?> editComplaint(@RequestPart(required = false, name = "complaintImage") List<MultipartFile> complaintImages, @RequestPart(value = "payloads", required = false) UpdateComplaint payloads, @PathVariable("hostelId") String hostelId, @PathVariable("complaintId") Integer complaintId) {
        return complaintsService.updateComplaint(complaintImages, payloads, hostelId,complaintId);
    }

    @DeleteMapping("/{hostelId}/{complaintId}")
    public ResponseEntity<?> deleteComplaint(@PathVariable("complaintId") Integer complaintId, @PathVariable("hostelId") String hostelId, @RequestBody DeleteComplaintRequest deleteComplaintRequest) {
        return complaintsService.deleteComplaint(complaintId,hostelId,deleteComplaintRequest);
    }

    @PostMapping("/comment/{complaintId}")
    public ResponseEntity<?> addComplaintComments(@PathVariable("complaintId") int complaintId,@Valid @RequestBody AddComplaintComment comment) {
        return complaintsService.addComplaintComments(comment,complaintId);
    }

    @DeleteMapping("/image/{complaintId}/{hostelId}/{imageId}")
    public ResponseEntity<?> deactivateComplaintImage(
            @PathVariable("complaintId") Integer complaintId,
            @PathVariable("imageId") Integer imageId,
            @PathVariable("hostelId") String hostelId
    ) {
        return complaintsService.deactivateComplaintImage(complaintId, imageId, hostelId);
    }


}
