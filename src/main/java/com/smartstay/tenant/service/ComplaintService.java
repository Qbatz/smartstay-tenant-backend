package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.config.FilesConfig;
import com.smartstay.tenant.config.UploadFileToS3;
import com.smartstay.tenant.dao.*;
import com.smartstay.tenant.dto.ComplaintDTO;
import com.smartstay.tenant.ennum.CustomerStatus;
import com.smartstay.tenant.repository.ComplaintsV1Repository;
import com.smartstay.tenant.response.complaints.AddComplaints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintsV1Repository complaintsV1Repository;
    @Autowired
    private UploadFileToS3 uploadToS3;
    @Autowired
    private ComplaintsV1Repository complaintRepository;


    @Autowired
    private Authentication authentication;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private HostelService hostelService;

    public ComplaintService(ComplaintsV1Repository complaintsV1Repository) {
        this.complaintsV1Repository = complaintsV1Repository;
    }

    public List<ComplaintDTO> getComplaints(String hostelId, String customerId) {
        return complaintsV1Repository.findComplaintsByHostelAndCustomer(hostelId, customerId);
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


    public ResponseEntity<?> addComplaint(MultipartFile complaintImage, AddComplaints request) {
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
        Floors floors = null;
        if (request.floorId() != null) {
            floors = hostelService.findByFloorIdAndHostelId(request.floorId(), request.hostelId());
            if (floors == null) {
                return new ResponseEntity<>("Floor not found in this hostel.", HttpStatus.BAD_REQUEST);
            }
            complaint.setFloorId(request.floorId());
        } else {
            complaint.setFloorId(0);
        }

        Rooms rooms = null;
        if (request.roomId() != null) {
            rooms = hostelService.findByRoomIdAndParentIdAndHostelId(request.roomId(), hostelV1.getParentId(), request.hostelId());
            if (rooms == null) {
                return new ResponseEntity<>("Room not found in this hostel.", HttpStatus.BAD_REQUEST);
            }
            if (floors != null) {
                Rooms roomInFloor = hostelService.findByRoomIdAndParentIdAndHostelIdAndFloorId(request.roomId(), hostelV1.getParentId(), request.hostelId(), request.floorId());
                if (roomInFloor == null) {
                    return new ResponseEntity<>("This room is not linked to the given floor.", HttpStatus.BAD_REQUEST);
                }
            }
            complaint.setRoomId(request.roomId());
        } else {
            complaint.setRoomId(0);
        }

        if (request.bedId() != null) {
            Beds bed = hostelService.findByBedIdAndParentIdAndHostelId(request.bedId(), hostelV1.getParentId(), request.hostelId());
            if (bed == null) {
                return new ResponseEntity<>("Bed not found in this hostel.", HttpStatus.BAD_REQUEST);
            }
            if (rooms != null) {
                Beds bedInRoom = hostelService.findByBedIdAndRoomIdAndParentId(request.bedId(), rooms.getRoomId(), hostelV1.getParentId());
                if (bedInRoom == null) {
                    return new ResponseEntity<>("This bed is not linked to the given room.", HttpStatus.BAD_REQUEST);
                }
            }
            if (floors != null && rooms != null) {
                Beds bedInFloorRoom = hostelService.findByBedIdAndRoomIdAndParentId(request.bedId(), rooms.getRoomId(), hostelV1.getParentId());
                if (bedInFloorRoom == null) {
                    return new ResponseEntity<>("This bed is not linked to the given floor and room combination.", HttpStatus.BAD_REQUEST);
                }
            }
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

        if (complaintImage != null) {
            String complaintUrl = uploadToS3.uploadFileToS3(FilesConfig.convertMultipartToFile(complaintImage), "Complaint-Images");
            complaint.setComplaintImageUrl(complaintUrl);
        }

        complaintRepository.save(complaint);

        return new ResponseEntity<>(Utils.CREATED, HttpStatus.CREATED);
    }
}
