package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.CountriesUtils;
import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.config.FilesConfig;
import com.smartstay.tenant.config.UploadFileToS3;
import com.smartstay.tenant.dao.*;
import com.smartstay.tenant.dao.ComplaintImages;
import com.smartstay.tenant.dto.ComplaintDTO;
import com.smartstay.tenant.dto.ComplaintDetails;
import com.smartstay.tenant.dto.comment.CommentsListResponse;
import com.smartstay.tenant.dto.comment.ComplaintCommentProjection;
import com.smartstay.tenant.dto.complaint.ComplaintResponse;
import com.smartstay.tenant.ennum.CommentSource;
import com.smartstay.tenant.ennum.ComplaintStatus;
import com.smartstay.tenant.ennum.CustomerStatus;
import com.smartstay.tenant.ennum.UserType;
import com.smartstay.tenant.mapper.comments.CommentsMapper;
import com.smartstay.tenant.mapper.complaint.ComplaintMapper;
import com.smartstay.tenant.mapper.complaint.ComplaintUpdatesMapper;
import com.smartstay.tenant.payload.complaint.AddComplaintComment;
import com.smartstay.tenant.payload.complaint.DeleteComplaintRequest;
import com.smartstay.tenant.payload.complaint.UpdateComplaint;
import com.smartstay.tenant.repository.*;
import com.smartstay.tenant.response.complaints.*;
import jdk.jshell.execution.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private UserService userService;

    @Autowired
    private Authentication authentication;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private HostelRepository hostelRepository;

    @Autowired
    private UserHostelService userHostelService;


    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ComplaintTypeService complaintTypeService;

    @Autowired
    private CommentRepository commentRepository;


    public List<ComplaintDTO> getComplaints(String hostelId, String customerId) {
        return complaintsV1Repository.findComplaintsByHostelAndCustomer(hostelId, customerId, PageRequest.of(0, 5));
    }

    public ResponseEntity<?> getComplaintByIdOld(String hostelId, Integer complaintId) {
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
        List<ComplaintImage> images = complaintImagesRepository.findImagesByComplaintId(complaintId);

        List<ComplaintCommentProjection> comments = commentsRepository.findCommentsByComplaintIds(complaintId);

        CommentsMapper commentsMapper = new CommentsMapper();
        List<CommentsListResponse> commentslist = comments.stream().map(commentsMapper).toList();

        ComplaintDetailsResponse complaintDetails = new ComplaintDetailsResponse(complaint.complaintId(), complaint.complaintTypeName(), complaint.complaintTypeId(), complaint.complaintDate(), complaint.description(), complaint.status(), complaint.assigneeName(), complaint.floorName(), complaint.roomName(), complaint.bedName(), complaint.customerName(), complaint.assignedDate(), complaint.createdBy(), complaint.hostelName(), complaint.assigneeMobileNumber(), images, commentslist);

        return new ResponseEntity<>(complaintDetails, HttpStatus.OK);
    }

    public ResponseEntity<?> getComplaintById(String hostelId, String c) {
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        Customers customers = customerService.getCustomerInformation(authentication.getName());
        if (customers == null) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        if (!customerService.existsByCustomerIdAndHostelId(authentication.getName(), hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        Integer complaintId = 0;
        try {
            complaintId = Integer.parseInt(c);
        }
        catch (Exception e) {
            return new ResponseEntity<>(Utils.INVALID_COMPLAINT_ID, HttpStatus.BAD_REQUEST);
        }
        if (complaintId == 0) {
            return new ResponseEntity<>(Utils.INVALID_COMPLAINT_ID, HttpStatus.BAD_REQUEST);
        }
        ComplaintsV1 complaintsV1 = complaintsV1Repository.findByComplaintIdAndHostelIdAndIsDeletedFalse(complaintId, hostelId);
        if (complaintsV1 == null) {
            return new ResponseEntity<>(Utils.INVALID_COMPLAINT_ID, HttpStatus.BAD_REQUEST);
        }

        ComplaintTypeV1 complaintTypeV1 = complaintTypeService.getComplaintTypeById(complaintsV1.getComplaintTypeId(), hostelId);
        if (complaintTypeV1 == null) {
            return new ResponseEntity<>(Utils.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
        }

        StringBuilder fullName = new StringBuilder();
        StringBuilder intls = new StringBuilder();
        if (customers.getFirstName() != null) {
            fullName.append(customers.getFirstName());
            intls.append(customers.getFirstName().toUpperCase().charAt(0));
        }
        if (customers.getLastName() != null && !customers.getLastName().trim().equalsIgnoreCase("")) {
            fullName.append(" ");
            fullName.append(customers.getLastName());
            intls.append(customers.getLastName().toUpperCase().charAt(0));
        }
        else {
            if (customers.getFirstName() != null && customers.getFirstName().length() > 1) {
                intls.append(customers.getFirstName().toUpperCase().charAt(1));
            }
        }

        CustomerDetails customerDetails = new CustomerDetails(customers.getCustomerId(),
                fullName.toString(),
                customers.getFirstName(),
                customers.getLastName(),
                customers.getProfilePic(),
                intls.toString(),
                CountriesUtils.COUNTR_CODE_INDIA,
                customers.getMobile());

        UserDetails userDetails = null;

        if (complaintsV1.getStatus().equalsIgnoreCase(ComplaintStatus.ASSIGNED.name())
                || complaintsV1.getStatus().equalsIgnoreCase(ComplaintStatus.PENDING.name())
                || complaintsV1.getStatus().equalsIgnoreCase(ComplaintStatus.RESOLVED.name())) {
            Users users = userService.findUserByUserId(complaintsV1.getAssigneeId());
            if (users != null) {
                StringBuilder uFullName = new StringBuilder();
                StringBuilder uInitials = new StringBuilder();

                if (users.getFirstName() != null) {
                    uFullName.append(users.getFirstName());
                    uInitials.append(users.getFirstName().toUpperCase().charAt(0));
                }
                if (users.getLastName() != null && !users.getLastName().trim().equalsIgnoreCase("")) {
                    uFullName.append(" ");
                    uFullName.append(users.getLastName());
                    uInitials.append(users.getLastName().toUpperCase().charAt(0));

                }
                else {
                    if (users.getFirstName().length() > 1) {
                        uInitials.append(users.getFirstName().toUpperCase().charAt(1));
                    }
                }

                userDetails = new UserDetails(users.getUserId(),
                        users.getFirstName(),
                        users.getLastName(),
                        uFullName.toString(),
                        uInitials.toString(),
                        users.getProfileUrl(),
                        users.getMobileNo(),
                        CountriesUtils.COUNTR_CODE_INDIA);

            }
        }

        List<com.smartstay.tenant.response.complaints.ComplaintImages> images = new ArrayList<>();

        if (complaintsV1.getAdditionalImages() != null) {
            images = complaintsV1
                    .getAdditionalImages()
                    .stream()
                    .map(i -> new com.smartstay.tenant.response.complaints.ComplaintImages(i.getId(), i.getImageUrl()))
                    .toList();
        }

        List<com.smartstay.tenant.dto.complaint.ComplaintComments> complaintComments = new ArrayList<>();

        if (complaintsV1.getComplaintComments() != null) {
            List<String> userId = complaintsV1.getComplaintComments()
                    .stream()
                    .filter(i -> !i.getUserType().equalsIgnoreCase(UserType.TENANT.name()))
                    .map(ComplaintComments::getCreatedBy)
                    .toList();

            List<String> tenantUserIds = complaintsV1.getComplaintComments()
                    .stream()
                    .filter(i -> i.getUserType().equalsIgnoreCase(UserType.TENANT.name()))
                    .map(ComplaintComments::getCreatedBy)
                    .toList();

            List<Users> adminUsers = userService.findAllUsersById(userId);
            List<Customers> tenants = customerService.findAllByListOfCustomers(tenantUserIds);

            complaintComments = complaintsV1.getComplaintComments()
                    .stream()
                    .map(i -> {
                        StringBuilder initials = new StringBuilder();
                        String profilePic = null;
                        if (i.getUserType().equalsIgnoreCase(UserType.TENANT.name())) {
                            if (!tenants.isEmpty()) {
                                Customers cus = tenants.stream()
                                        .filter(tnt -> tnt.getCustomerId().equalsIgnoreCase(i.getCreatedBy()))
                                        .findFirst()
                                        .orElse(null);
                                if (cus != null) {
                                    profilePic = cus.getProfilePic();
                                    if (cus.getFirstName() != null) {
                                        initials.append(cus.getFirstName().trim().toUpperCase().charAt(0));
                                    }
                                    if (cus.getLastName() != null && !cus.getLastName().trim().equalsIgnoreCase("")) {
                                        initials.append(cus.getLastName().trim().toUpperCase().charAt(0));
                                    }
                                    else if (cus.getFirstName() != null) {
                                        if (cus.getFirstName().length() > 1) {
                                            initials.append(cus.getFirstName().trim().toUpperCase().charAt(1));
                                        }
                                    }

                                }
                            }
                        }
                        else {
                            if (!adminUsers.isEmpty()) {
                                Users admUsr = adminUsers.stream()
                                        .filter(tnt -> tnt.getUserId().equalsIgnoreCase(i.getCreatedBy()))
                                        .findFirst()
                                        .orElse(null);
                                if (admUsr != null) {
                                    profilePic = admUsr.getProfileUrl();
                                    if (admUsr.getFirstName() != null) {
                                        initials.append(admUsr.getFirstName().trim().toUpperCase().charAt(0));
                                    }
                                    if (admUsr.getLastName() != null && !admUsr.getLastName().trim().equalsIgnoreCase("")) {
                                        initials.append(admUsr.getLastName().trim().toUpperCase().charAt(0));
                                    }
                                    else if (admUsr.getFirstName() != null) {
                                        if (admUsr.getFirstName().length() > 1) {
                                            initials.append(admUsr.getFirstName().trim().toUpperCase().charAt(1));
                                        }
                                    }
                                }
                            }
                        }
                        return new com.smartstay.tenant.dto.complaint.ComplaintComments(i.getComment(),
                                fullName.toString(),
                                initials.toString(),
                                profilePic,
                               Utils.dateToString(i.getCreatedAt()),
                                Utils.dateToTime(i.getCreatedAt()));
                    })
                    .toList();
        }

        com.smartstay.tenant.response.complaints.ComplaintDetails complaintDetails = new com.smartstay.tenant.response.complaints.ComplaintDetails(complaintsV1.getComplaintId(),
                complaintsV1.getDescription(),
                complaintsV1.getComplaintTypeId(),
                complaintTypeV1.getComplaintTypeName(),
                complaintsV1.getStatus(),
                Utils.dateToString(complaintsV1.getComplaintDate()),
                Utils.dateToTime(complaintsV1.getComplaintDate()),
                customerDetails,
                userDetails,
                images,
                complaintComments);


        return new ResponseEntity<>(complaintDetails, HttpStatus.OK);
    }


    public ResponseEntity<?> getComplaintList(String hostelId, int page, int size) {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Utils.UNAUTHORIZED);
        }
        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        Pageable pageable = PageRequest.of(page, size);

        Page<ComplaintDTO> complaints = complaintsV1Repository.getAllComplaints(hostelId, customerId, pageable);

        if (complaints.isEmpty()) {
            return new ResponseEntity<>(Utils.COMPLAINTS_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        ComplaintMapper complaintMapper = new ComplaintMapper();
        Page<ComplaintResponse> response = complaints.map(complaintMapper);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public ResponseEntity<?> addComplaint(List<MultipartFile> complaintImages, AddComplaints request, String hostelId) {

        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
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

        boolean customerExist = customerService.existsByHostelIdAndCustomerIdAndStatusesIn(hostelId, customerId, currentStatus);
        if (!customerExist) {
            return new ResponseEntity<>(Utils.CUSTOMER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        ComplaintTypeV1 complaintTypeV1 = complaintTypeService.getComplaintTypeById(request.complaintTypeId(), hostelId);
        if (complaintTypeV1 == null) {
            return new ResponseEntity<>(Utils.COMPLAINT_TYPE_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        List<ComplaintsV1> existingComplaint = complaintsV1Repository.findExistingOpenComplaint(customerId, hostelId, request.complaintTypeId(), List.of(ComplaintStatus.OPENED.name(), ComplaintStatus.PENDING.name(), ComplaintStatus.ASSIGNED.name()));

        if (existingComplaint != null && !existingComplaint.isEmpty()) {
            return new ResponseEntity<>("A complaint of this type is already open. Please wait until it is resolved.", HttpStatus.BAD_REQUEST);
        }
        HostelV1 hostelV1 = hostelRepository.findById(hostelId).orElse(null);
        complaint.setCustomerId(customerId);
        complaint.setComplaintTypeId(request.complaintTypeId());
        complaint.setComplaintDate(new Date());
        complaint.setDescription(request.description());
        complaint.setCreatedAt(new Date());
        complaint.setUpdatedAt(new Date());
        complaint.setCreatedBy(customerId);
        complaint.setHostelId(hostelId);
        complaint.setIsActive(true);
        complaint.setStatus(ComplaintStatus.OPENED.name());
        complaint.setIsDeleted(false);
        if (hostelV1 != null) {
            complaint.setParentId(hostelV1.getParentId());
        }
        List<String> listImageUrls = new ArrayList<>();
        if (complaintImages != null && !complaintImages.isEmpty()) {
            listImageUrls = complaintImages.stream().map(multipartFile -> uploadToS3.uploadFileToS3(FilesConfig.convertMultipartToFileNew(multipartFile), "CustomerComplaint-Images")).toList();
        }
        if (!listImageUrls.isEmpty()) {
            List<ComplaintImages> complaintImagesList = listImageUrls.stream().map(item -> {
                ComplaintImages complaintImages1 = new ComplaintImages();
                complaintImages1.setIsActive(true);
                complaintImages1.setIsDeleted(false);
                complaintImages1.setCreatedAt(new Date());
                complaintImages1.setCreatedBy(customerId);
                complaintImages1.setImageUrl(item);
                complaintImages1.setComplaints(complaint);
                return complaintImages1;
            }).toList();

            complaint.setAdditionalImages(complaintImagesList);
        }

        ComplaintUpdates complaintUpdates = new ComplaintUpdates();
        complaintUpdates.setStatus(ComplaintStatus.OPENED.name());
        complaintUpdates.setUserType(UserType.TENANT.name());
        complaintUpdates.setUpdatedBy(authentication.getName());
        complaintUpdates.setComments("Created a complaint");
        complaintUpdates.setComplaint(complaint);
        complaintUpdates.setCreatedAt(new Date());


        List<ComplaintUpdates> listComplaintUpdates = new ArrayList<>();
        listComplaintUpdates.add(complaintUpdates);
        complaint.setComplaintUpdates(listComplaintUpdates);

        ComplaintsV1 savedComplaint = complaintsV1Repository.save(complaint);

        notificationService.createNotificationForComplaint(customerId, hostelId, savedComplaint.getComplaintId().toString(), complaintTypeV1.getComplaintTypeName(), request.description());

        return new ResponseEntity<>(Utils.CREATED, HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateComplaint(List<MultipartFile> complaintImages, UpdateComplaint request, String hostelId, Integer complaintId) {

        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        ComplaintsV1 complaint = complaintsV1Repository.findByComplaintIdAndHostelIdAndCustomerId(complaintId, hostelId, customerId);
        if (complaint == null) {
            return new ResponseEntity<>(Utils.COMPLAINTS_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        if (request != null && request.floorId() != null) {
            complaint.setFloorId(request.floorId());
        }

        if (request != null && request.roomId() != null) {
            complaint.setRoomId(request.roomId());
        }

        if (request != null && request.bedId() != null) {
            complaint.setBedId(request.bedId());

        }
        List<String> currentStatus = Arrays.asList(CustomerStatus.CHECK_IN.name(), CustomerStatus.NOTICE.name());


        boolean customerExist = customerService.existsByHostelIdAndCustomerIdAndStatusesIn(hostelId, customerId, currentStatus);
        if (!customerExist) {
            return new ResponseEntity<>(Utils.CUSTOMER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        if (request != null && request.complaintTypeId() != null) {
            ComplaintTypeV1 complaintTypeV1Check = complaintTypeService.getComplaintTypeById(request.complaintTypeId(), hostelId);
            if (complaintTypeV1Check == null) {
                return new ResponseEntity<>(Utils.COMPLAINT_TYPE_NOT_FOUND, HttpStatus.BAD_REQUEST);
            }

            List<ComplaintsV1> existingComplaint = complaintsV1Repository.findExistingOpenComplaintForEdit(complaintId, customerId, hostelId, request.complaintTypeId(), List.of(ComplaintStatus.OPENED.name(), ComplaintStatus.PENDING.name(), ComplaintStatus.ASSIGNED.name()));

            if (existingComplaint != null && !existingComplaint.isEmpty()) {
                return new ResponseEntity<>("A complaint of this type is already open. Please wait until it is resolved.", HttpStatus.BAD_REQUEST);
            }

            complaint.setComplaintTypeId(request.complaintTypeId());
        }

        if (request != null && request.description() != null) {
            complaint.setDescription(request.description());
        }
        complaint.setUpdatedAt(new Date());

        if (request != null && request.isActive() != null) {
            complaint.setIsActive(request.isActive());
        }

        List<String> listImageUrls = new ArrayList<>();
        if (complaintImages != null && !complaintImages.isEmpty()) {
            listImageUrls = complaintImages.stream().map(multipartFile -> uploadToS3.uploadFileToS3(FilesConfig.convertMultipartToFileNew(multipartFile), "CustomerComplaint-Images")).toList();

        }

        if (!listImageUrls.isEmpty()) {
            List<ComplaintImages> complaintImagesList = complaint.getAdditionalImages();

            if (complaintImages == null || complaintImages.isEmpty()) {
                complaintImagesList = listImageUrls.stream().map(item -> {
                    ComplaintImages complaintImages1 = new ComplaintImages();
                    complaintImages1.setIsActive(true);
                    complaintImages1.setIsDeleted(false);
                    complaintImages1.setCreatedAt(new Date());
                    complaintImages1.setCreatedBy(customerId);
                    complaintImages1.setImageUrl(item);
                    complaintImages1.setComplaints(complaint);
                    return complaintImages1;
                }).toList();
            } else {

                complaintImagesList.addAll(listImageUrls.stream().map(item -> {
                    ComplaintImages complaintImages1 = new ComplaintImages();
                    complaintImages1.setIsActive(true);
                    complaintImages1.setIsDeleted(false);
                    complaintImages1.setCreatedAt(new Date());
                    complaintImages1.setCreatedBy(customerId);
                    complaintImages1.setImageUrl(item);
                    complaintImages1.setComplaints(complaint);
                    return complaintImages1;
                }).toList());
            }

            complaint.setAdditionalImages(complaintImagesList);
        }
        complaintsV1Repository.save(complaint);
        ComplaintTypeV1 complaintTypeV1 = complaintTypeService.getComplaintTypeById(complaintId, hostelId);
        String title = null;
        if (complaintTypeV1 != null) {
            title = "Complaint updated for " + complaintTypeV1.getComplaintTypeName();
        }
        notificationService.findAndUpdateComplaints(complaint, title);
        return new ResponseEntity<>(Utils.UPDATED, HttpStatus.OK);
    }


    public ResponseEntity<?> deleteComplaint(Integer complaintId, String hostelId, DeleteComplaintRequest request) {
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

        if (request.message() != null) {
            Comments comments = new Comments();
            comments.setComment(request.message());
            comments.setSource(CommentSource.COMPLAINT.name());
            comments.setSourceId(complaintId.toString());
            comments.setCreatedAt(new Date());
            comments.setIsActive(true);
            comments.setIsDeleted(false);
            comments.setUserId(customerId);
            commentRepository.save(comments);
        }
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
        ComplaintsV1 complaintExist = complaintsV1Repository.findByComplaintIdAndCustomerIdAndHostelId(complaintId, customerId, request.hostelId());
        if (complaintExist == null) {
            return new ResponseEntity<>(Utils.COMPLAINTS_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        ComplaintComments complaintComments = new ComplaintComments();
        complaintComments.setCommentDate(new Date());
        complaintComments.setComplaint(complaintExist);
        complaintComments.setComment(request.message());
        complaintComments.setIsActive(true);
        complaintComments.setComplaintStatus(complaintExist.getStatus());
        complaintComments.setUserType(UserType.TENANT.name());
        complaintComments.setCreatedBy(customerId);
        complaintComments.setUserName(customers.getFirstName() + " " + customers.getLastName());
        complaintComments.setCreatedAt(new Date());
        commentsRepository.save(complaintComments);

        if (complaintExist.getStatus().equalsIgnoreCase(ComplaintStatus.ASSIGNED.name())) {
            String assigneeId = complaintExist.getAssigneeId();
            ComplaintTypeV1 complaintTypeV1 = complaintTypeService.getComplaintTypeById(complaintExist.getComplaintTypeId(), complaintExist.getHostelId());
            notificationService.sendPushNotification(assigneeId, complaintTypeV1.getComplaintTypeName());
        }


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

            ComplaintsV1 complaint = complaintsV1Repository.findByComplaintIdAndHostelIdAndIsDeletedFalse(complaintId, hostelId);
            if (complaint == null) {
                return new ResponseEntity<>(Utils.COMPLAINT_NOT_FOUND, HttpStatus.BAD_REQUEST);
            }

            ComplaintImages image = complaintImagesRepository.findByIdAndComplaintIdAndIsDeletedFalse(imageId, complaintId);
            if (image == null) {
                return new ResponseEntity<>(Utils.COMPLAINT_IMAGE_NOT_FOUND, HttpStatus.BAD_REQUEST);
            }
            image.setIsActive(false);
            image.setIsDeleted(true);
            image.setUpdatedAt(new Date());
            complaintImagesRepository.save(image);

            return ResponseEntity.ok(Utils.DELETED);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to deactivate complaint image.");
        }
    }

    public ResponseEntity<?> getUpdates(String hostelId, String complaintId) {
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        Customers customers = customerService.getCustomerById(authentication.getName());
        if (customers == null) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        Integer cId = 0;
        if (complaintId == null) {
            return new ResponseEntity<>(Utils.COMPLAINTS_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        try {
            cId = Integer.parseInt(complaintId);
        }
        catch (Exception e) {
            return new ResponseEntity<>(Utils.COMPLAINTS_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        ComplaintsV1 complaints = complaintsV1Repository.findById(cId).orElse(null);
        if (complaints == null) {
            return new ResponseEntity<>(Utils.COMPLAINTS_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        if (!complaints.getHostelId().equalsIgnoreCase(hostelId)) {
            return new ResponseEntity<>(Utils.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
        }
        if (!customerService.existsByCustomerIdAndHostelId(customers.getCustomerId(), hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        ComplaintTypeV1 complaintTypeV1 = complaintTypeService.getComplaintTypeById(complaints.getComplaintTypeId(), hostelId);
        if (complaintTypeV1 == null) {
            return new ResponseEntity<>(Utils.COMPLAINTS_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        String complaintTypeName = complaintTypeV1.getComplaintTypeName();
        List<ComplaintUpdates> listComplaintUpdates = complaints.getComplaintUpdates();
        List<ComplaintComments> listComplaintComments = complaints.getComplaintComments();

        List<String> updatedByAdminUsers = new ArrayList<>(listComplaintUpdates
                .stream()
                .filter(i -> !i.getUserType().equalsIgnoreCase(UserType.TENANT.name()))
                .map(ComplaintUpdates::getUpdatedBy)
                .toList());
        List<String> updatedByTenantUsers = new ArrayList<>(listComplaintUpdates
                .stream()
                .filter(i -> i.getUserType().equalsIgnoreCase(UserType.TENANT.name()))
                .map(ComplaintUpdates::getUpdatedBy)
                .toList());
        updatedByAdminUsers.addAll(listComplaintComments
                .stream()
                .filter(i -> !i.getUserType().equalsIgnoreCase(UserType.TENANT.name()))
                .map(ComplaintComments::getCreatedBy)
                .toList());
        updatedByTenantUsers.addAll(listComplaintComments
                .stream()
                .filter(i -> i.getUserType().equalsIgnoreCase(UserType.TENANT.name()))
                .map(ComplaintComments::getCreatedBy)
                .toList());

        List<Users> adminUsers = userService.findAllUsersById(updatedByAdminUsers);
        List<Customers> tenantUsers = customerService.getCustomerDetails(updatedByTenantUsers);

        List<String> assignedUsersIds = listComplaintUpdates
                .stream()
                .filter(i -> i.getStatus().equalsIgnoreCase(ComplaintStatus.ASSIGNED.name()))
                .map(ComplaintUpdates::getAssignedTo)
                .toList();
        List<Users> assignedUsers = userService.findAllUsersById(assignedUsersIds);

        List<ComplaintUpdatesList> listComments =
                listComplaintUpdates
                        .stream()
                        .map(i -> new ComplaintUpdatesMapper(tenantUsers, adminUsers, listComplaintComments, complaints.getDescription(), assignedUsers, complaintTypeName).apply(i))
                        .toList();
        ComplaintsUpdates updates = new ComplaintsUpdates(complaintId, complaints.getStatus(), listComments);
        return new ResponseEntity<>(updates, HttpStatus.OK);
    }
}
