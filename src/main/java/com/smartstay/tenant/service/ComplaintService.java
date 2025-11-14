package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.config.FilesConfig;
import com.smartstay.tenant.config.UploadFileToS3;
import com.smartstay.tenant.dao.*;
import com.smartstay.tenant.dto.ComplaintDTO;
import com.smartstay.tenant.dto.ComplaintDetails;
import com.smartstay.tenant.ennum.CustomerStatus;
import com.smartstay.tenant.payload.complaint.AddComplaintComment;
import com.smartstay.tenant.repository.ComplaintCommentsRepository;
import com.smartstay.tenant.repository.ComplaintImagesRepository;
import com.smartstay.tenant.repository.ComplaintsV1Repository;
import com.smartstay.tenant.response.complaints.AddComplaints;
import com.smartstay.tenant.response.complaints.ComplaintComment;
import com.smartstay.tenant.response.complaints.ComplaintImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class ComplaintService {


    @Autowired
    private UploadFileToS3 uploadToS3;
    @Autowired
    private ComplaintsV1Repository complaintsV1Repository;

    @Autowired
    private ComplaintImagesRepository complaintImagesRepository;

    @Autowired
    private ComplaintCommentsRepository commentsRepository;


    @Autowired
    private Authentication authentication;

    @Autowired
    private CustomerService customerService;


    public List<ComplaintDTO> getComplaints(String hostelId, String customerId) {
        return complaintsV1Repository.findComplaintsByHostelAndCustomer(hostelId, customerId, PageRequest.of(0, 5));
    }

    public ResponseEntity<?> getComplaintById(String hostelId, Integer complaintId) {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Utils.UNAUTHORIZED);
        }

        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        ComplaintDetails complaint = complaintsV1Repository.getComplaintById(hostelId, customerId, complaintId);

        if (complaint == null) {
            return new ResponseEntity<>(Utils.COMPLAINT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        List<ComplaintImage> images =
                complaintsV1Repository.findImagesByComplaintId(complaintId);

        List<ComplaintComment> comments =
                complaintsV1Repository.findCommentsByComplaintId(complaintId);

        ComplaintDetails complaintDetails = new ComplaintDetails(
                complaint.complaintId(),
                complaint.complaintTypeName(),
                complaint.complaintDate(),
                complaint.description(),
                complaint.status(),
                complaint.assigneeName(),
                complaint.floorName(),
                complaint.roomName(),
                complaint.bedName(),
                complaint.customerName(),
                complaint.assignedDate(),
                complaint.createdBy(),
                complaint.hostelName(),
                complaint.assigneeMobileNumber(),
                images,
                comments
        );

        return new ResponseEntity<>(complaintDetails, HttpStatus.OK);
    }


    public ResponseEntity<?> getComplaintList(String hostelId) {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Utils.UNAUTHORIZED);
        }
        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        List<ComplaintDTO> complaints = complaintsV1Repository.getAllComplaints(hostelId, customerId);

        if (complaints.isEmpty()) {
            return new ResponseEntity<>(Utils.COMPLAINTS_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(complaints, HttpStatus.OK);
    }


    public ResponseEntity<?> addComplaint(List<MultipartFile> complaintImages, AddComplaints request) {
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("Invalid user.", HttpStatus.UNAUTHORIZED);
        }
        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, request.hostelId())) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        HostelV1 hostelV1 = customerService.findByCustomerIdAndHostelId(customerId, request.hostelId());
        if (hostelV1 == null) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }


        ComplaintsV1 complaint = new ComplaintsV1();
        if (request.floorId() != null) {
            complaint.setFloorId(request.floorId());
        } else {
            complaint.setFloorId(0);
        }

        if (request.roomId() != null) {
            complaint.setRoomId(request.roomId());
        } else {
            complaint.setRoomId(0);
        }

        if (request.bedId() != null) {
            complaint.setBedId(request.bedId());

        } else {
            complaint.setBedId(0);
        }
        List<String> currentStatus = Arrays.asList(CustomerStatus.CHECK_IN.name(), CustomerStatus.NOTICE.name());


        boolean customerExist = customerService.existsByHostelIdAndCustomerIdAndStatusesIn(request.hostelId(), request.customerId(), currentStatus);
        if (!customerExist) {
            return new ResponseEntity<>("Customer not found.", HttpStatus.BAD_REQUEST);
        }

        complaint.setCustomerId(request.customerId());
        complaint.setComplaintTypeId(request.complaintTypeId());
        if (request.complaintDate() != null) {
            String formattedDate = request.complaintDate().replace("/", "-");
            complaint.setComplaintDate(Utils.stringToDate(formattedDate, Utils.USER_INPUT_DATE_FORMAT));
        }
        complaint.setDescription(request.description());
        complaint.setCreatedAt(new Date());
        complaint.setUpdatedAt(new Date());
        complaint.setCreatedBy(customerId);
        complaint.setParentId(hostelV1.getParentId());
        complaint.setHostelId(request.hostelId());
        complaint.setIsActive(true);
        complaint.setStatus("PENDING");
        complaint.setIsDeleted(false);


        List<String> listImageUrls = new ArrayList<>();
        if (complaintImages != null && !complaintImages.isEmpty()) {
            listImageUrls = complaintImages.stream().map(multipartFile -> uploadToS3.uploadFileToS3(FilesConfig.convertMultipartToFile(multipartFile), "CustomerComplaint-Images")).toList();
        }
        if (!listImageUrls.isEmpty()) {
            List<ComplaintImages> complaintImagesList = listImageUrls.stream().map(item -> {
                ComplaintImages complaintImages1 = new ComplaintImages();
                complaintImages1.setCreatedBy(customerId);
                complaintImages1.setImageUrl(item);
                complaintImages1.setComplaints(complaint);
                return complaintImages1;
            }).toList();

            complaint.setAdditionalImages(complaintImagesList);
        }

        complaintsV1Repository.save(complaint);

        return new ResponseEntity<>(Utils.CREATED, HttpStatus.CREATED);
    }


    public ResponseEntity<?> deleteComplaint(Integer complaintId, String hostelId) {
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("Invalid user.", HttpStatus.UNAUTHORIZED);
        }

        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        ComplaintsV1 complaint = complaintsV1Repository.findById(complaintId).orElse(null);
        if (complaint == null) {
            return new ResponseEntity<>(Utils.COMPLAINTS_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        complaint.setUpdatedAt(new Date());
        complaint.setIsDeleted(true);
        complaint.setIsActive(false);
        complaintsV1Repository.save(complaint);
        return new ResponseEntity<>(Utils.DELETED, HttpStatus.OK);
    }


    public ResponseEntity<?> addComplaintComments(@RequestBody AddComplaintComment request, int complaintId) {
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("Invalid user.", HttpStatus.UNAUTHORIZED);
        }

        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, request.hostelId())) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        Customers customers = customerService.getCustomerById(customerId);
        ComplaintsV1 complaintExist = complaintsV1Repository.findByComplaintIdAndCustomerId(complaintId, request.hostelId());
        if (complaintExist == null) {
            return new ResponseEntity<>(Utils.COMPLAINTS_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        ComplaintComments complaintComments = new ComplaintComments();
        complaintComments.setCommentDate(new Date());
        complaintComments.setComplaint(complaintExist);
        complaintComments.setComment(request.message());
        complaintComments.setIsActive(true);
        complaintComments.setCreatedBy(customerId);
        complaintComments.setUserName(customers.getFirstName() + " " + customers.getLastName());
        complaintComments.setCreatedAt(new Date());
        commentsRepository.save(complaintComments);

        return new ResponseEntity<>(Utils.CREATED, HttpStatus.CREATED);
    }

    public ResponseEntity<?> deactivateComplaintImage(Integer complaintId, Integer imageId, String hostelId) {
        try {
            if (!authentication.isAuthenticated()) {
                return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
            }
            String customerId = authentication.getName();
            if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
                return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
            }

            ComplaintsV1 complaint = complaintsV1Repository
                    .findByComplaintIdAndHostelIdAndIsDeletedFalse(complaintId, hostelId);
            if (complaint == null) {
                return new ResponseEntity<>(Utils.COMPLAINT_NOT_FOUND, HttpStatus.BAD_REQUEST);
            }

            ComplaintImages image = complaintImagesRepository
                    .findByIdAndComplaintIdAndIsDeletedFalse(imageId, complaintId);
            if (image == null) {
                return new ResponseEntity<>(Utils.COMPLAINT_IMAGE_NOT_FOUND, HttpStatus.BAD_REQUEST);
            }
            image.setIsActive(false);
            image.setIsDeleted(true);
            image.setUpdatedAt(new Date());
            complaintImagesRepository.save(image);

            return ResponseEntity.ok(Utils.DELETED);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to deactivate complaint image.");
        }
    }



}
