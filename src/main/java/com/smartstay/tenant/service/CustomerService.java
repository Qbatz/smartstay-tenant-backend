package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Constants;
import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.config.FilesConfig;
import com.smartstay.tenant.config.UploadFileToS3;
import com.smartstay.tenant.dao.*;
import com.smartstay.tenant.ennum.CustomerStatus;
import com.smartstay.tenant.ennum.Gender;
import com.smartstay.tenant.ennum.RequestStatus;
import com.smartstay.tenant.ennum.UserType;
import com.smartstay.tenant.mapper.CustomerMapper;
import com.smartstay.tenant.payload.customer.CustomerAdditionalContactsEditPayload;
import com.smartstay.tenant.payload.customer.CustomerMpinOtpPayload;
import com.smartstay.tenant.payload.customer.CustomerMpinPayload;
import com.smartstay.tenant.payload.customer.RaiseNoticePayload;
import com.smartstay.tenant.repository.CustomerRepository;
import com.smartstay.tenant.repository.InvoicesV1Repository;
import com.smartstay.tenant.response.customer.EditCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Value("${ENVIRONMENT}")
    private String environment;

    @Autowired
    private InvoicesV1Repository invoicesV1Repository;
    @Autowired
    private HostelConfigService hostelConfigService;
    @Autowired
    CustomerRepository customersRepository;
    @Autowired
    Authentication authentication;
    @Autowired
    private BookingsService bookingsService;
    @Autowired
    private UploadFileToS3 uploadToS3;
    @Autowired
    private CustomerDocumentService customerDocumentService;
    @Autowired
    private HostelDuplicateService hostelService;
    @Autowired
    private CustomerAdditionalContactsService customerAdditionalContactsService;
    @Autowired
    private CustomerJobDetailsService customerJobDetailsService;
    @Autowired
    private CustomerCredentialsService customerCredentialsService;
    @Autowired
    private CustomersOtpService customersOtpService;
    @Autowired
    private OtpService otpService;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private RaiseNoticeRequestService raiseNoticeRequestService;
    @Autowired
    @Lazy
    private NotificationService notificationService;
    @Autowired
    @Lazy
    private FCMNotificationService fcmNotificationService;

    public ResponseEntity<?> getCustomerDetails() {

        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Utils.UNAUTHORIZED);
        }

        String customerId = authentication.getName();
        Customers customer = customersRepository.findById(customerId).orElse(null);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.CUSTOMER_NOT_FOUND);
        }

        HostelV1 hostel = hostelService.getHostelById(customer.getHostelId());

        List<CustomerAdditionalContacts> additionalContacts = customerAdditionalContactsService
                .getAdditionalContactsByCustomerId(customerId);

        List<CustomerDocuments> customerDocuments = customerDocumentService
                .getDocumentsByCustomerId(customerId);

        List<CustomerJobDetails> customerJobDetailsList = customerJobDetailsService
                .getCustomerJobDetailsByCustomerId(customerId);

        return new ResponseEntity<>(new CustomerMapper()
                .toDetailsDto(customer, additionalContacts,
                        bookingsService.getCustomerBookingDetails(customerId),
                        hostel, customerDocuments, customerJobDetailsList), HttpStatus.OK);
    }

    public List<Customers> getCustomerDetails(List<String> customerIds) {
        if (!customerIds.isEmpty()) {
            return customersRepository.findByCustomerIdIn(customerIds);
        }
        return null;
    }

    public Customers getCustomerInformation(String customerId) {
        return customersRepository.findById(customerId).orElse(null);
    }

    boolean existsByCustomerIdAndHostelId(String customerId, String hostelId) {
        return customersRepository.existsByCustomerIdAndHostelId(customerId, hostelId);
    }

    Customers getCustomerById(String customerId) {
        return customersRepository.findById(customerId).orElse(null);
    }

    boolean existsByHostelIdAndCustomerIdAndStatusesIn(String hostelId, String customerId, List<String> statuses) {
        return customersRepository.existsByHostelIdAndCustomerIdAndStatusesIn(hostelId, customerId, statuses);
    }

    public ResponseEntity<?> updateCustomerInfo(EditCustomer updateInfo, MultipartFile file) {

        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        String customerId = authentication.getName();
        Customers customers = customersRepository.findById(customerId).orElse(null);
        if (customers == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Utils.CUSTOMER_NOT_FOUND);
        }

        if (updateInfo != null) {

            String profileImage = null;
            if (file != null) {
                profileImage = uploadToS3
                        .uploadFileToS3(FilesConfig.convertMultipartToFileNew(file), "customer/profile");
                customers.setProfilePic(profileImage);
            }

            if (updateInfo.firstName() != null && !updateInfo.firstName().equalsIgnoreCase("")) {
                customers.setFirstName(updateInfo.firstName());
            }
            if (updateInfo.lastName() != null && !updateInfo.lastName().equalsIgnoreCase("")) {
                customers.setLastName(updateInfo.lastName());
            }
            if (updateInfo.emailId() != null && !updateInfo.emailId().equalsIgnoreCase("")) {
                customers.setEmailId(updateInfo.emailId());
            }
            if (updateInfo.houseNo() != null && !updateInfo.houseNo().equalsIgnoreCase("")) {
                customers.setHouseNo(updateInfo.houseNo());
            }
            if (updateInfo.street() != null && !updateInfo.street().equalsIgnoreCase("")) {
                customers.setStreet(updateInfo.street());
            }
            if (updateInfo.landmark() != null && !updateInfo.landmark().equalsIgnoreCase("")) {
                customers.setLandmark(updateInfo.landmark());
            }
            if (updateInfo.pincode() != null){
                if (updateInfo.pincode() < 0) {
                    return new ResponseEntity<>(Utils.INVALID_PINCODE, HttpStatus.BAD_REQUEST);
                }
                customers.setPincode(updateInfo.pincode());
            }
            if (updateInfo.city() != null && !updateInfo.city().equalsIgnoreCase("")) {
                customers.setCity(updateInfo.city());
            }
            if (updateInfo.state() != null && !updateInfo.state().equalsIgnoreCase("")) {
                customers.setState(updateInfo.state());
            }
            if (updateInfo.dob() != null && !updateInfo.dob().equalsIgnoreCase("")) {
                String formattedDate = updateInfo.dob().replace("/", "-");
                customers.setDateOfBirth(Utils.stringToDate(formattedDate, Utils.USER_INPUT_DATE_FORMAT));
            }
            if (updateInfo.gender() != null && !updateInfo.gender().equalsIgnoreCase("")) {
                try {
                    Gender gender = Gender.valueOf(updateInfo.gender().toUpperCase());
                    customers.setGender(gender.getLabel());
                } catch (IllegalArgumentException e) {
                    return new ResponseEntity<>(Utils.INVALID_GENDER_VALUE, HttpStatus.BAD_REQUEST);
                }
            }

            customersRepository.save(customers);

            if (updateInfo.additionalContacts() != null && !updateInfo.additionalContacts().isEmpty()) {

                Set<Long> contactIds = updateInfo.additionalContacts().stream()
                        .map(CustomerAdditionalContactsEditPayload::contactId)
                        .collect(Collectors.toSet());

                List<CustomerAdditionalContacts> additionalContacts = customerAdditionalContactsService
                        .getAdditionalContactsByCustomerIdAndContactIds(customerId, contactIds);

                Map<Long, CustomerAdditionalContacts> additionalContactsMap = additionalContacts.stream()
                        .collect(Collectors.toMap(CustomerAdditionalContacts::getContactId,
                                additionalContact -> additionalContact));

                for (CustomerAdditionalContactsEditPayload additionalContactPayload :  updateInfo.additionalContacts()) {

                    CustomerAdditionalContacts additionalContact = additionalContactsMap
                            .getOrDefault(additionalContactPayload.contactId(), null);

                    if (additionalContact != null) {
                        if (additionalContactPayload.name() != null && !additionalContactPayload.name().equalsIgnoreCase("")) {
                            additionalContact.setName(additionalContactPayload.name());
                        }
                        if (additionalContactPayload.relationship() != null && !additionalContactPayload.relationship().equalsIgnoreCase("")) {
                            additionalContact.setRelationship(additionalContactPayload.relationship());
                        }
                        if (additionalContactPayload.occupation() != null && !additionalContactPayload.occupation().equalsIgnoreCase("")) {
                            additionalContact.setOccupation(additionalContactPayload.occupation());
                        }
                        if (additionalContactPayload.mobile() != null && !additionalContactPayload.mobile().equalsIgnoreCase("")) {
                            additionalContact.setMobile(additionalContactPayload.mobile());
                        }
                        if (additionalContactPayload.fullAddress() != null && !additionalContactPayload.fullAddress().equalsIgnoreCase("")) {
                            additionalContact.setFullAddress(additionalContactPayload.fullAddress());
                        }
                        if (additionalContactPayload.countryCode() != null && !additionalContactPayload.countryCode().equalsIgnoreCase("")) {
                            additionalContact.setCountryCode(additionalContactPayload.countryCode());
                        }
                        additionalContact.setUpdatedByUserType(UserType.TENANT.name());
                        additionalContact.setUpdatedBy(customerId);
                        additionalContact.setUpdatedAt(new Date());
                    }
                }

                customerAdditionalContactsService.saveAll(additionalContacts);
            }

            return new ResponseEntity<>(Utils.UPDATED, HttpStatus.OK);

        } else {
            return new ResponseEntity<>(Utils.PAYLOADS_REQUIRED, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getRentDetails(String hostelId) {

        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        String customerId = authentication.getName();
        if (!existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        BookingsV1 bookingDetails = bookingsService.getLatestBooking(customerId, hostelId);
        if (bookingDetails == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Booking details not found");
        }

        Double rentAmount = bookingDetails.getRentAmount();
        Double advancePaidAmount = invoicesV1Repository.findAdvancePaidAmount(customerId);
        if (advancePaidAmount == null) {
            advancePaidAmount = 0.0;
        }

        BillingRules billingRules = hostelConfigService.getLatestBillRuleByHostelIdAndStartDate(hostelId, new Date());

//        int dueDay = billingRules != null ? billingRules.getBillingDueDate() : 0;
        String dueDateText =  " 1st of every month";
        if (billingRules != null) {
            if (billingRules.getBillingStartDate() == 1) {
                dueDateText = billingRules.getBillingStartDate()+ "st of every month";
            }
            else if (billingRules.getBillingStartDate() == 2){
                dueDateText = billingRules.getBillingStartDate()+ "nd of every month";
            }
            else if (billingRules.getBillingStartDate() == 3){
                dueDateText = billingRules.getBillingStartDate()+ "rd of every month";
            }else {
                dueDateText = billingRules.getBillingStartDate()+ "th of every month";
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("joiningDate", Utils.dateToString(bookingDetails.getJoiningDate()));
        response.put("rentAmount", rentAmount );
        response.put("advancePaidAmount", advancePaidAmount);
        response.put("dueDate", dueDateText);

        return ResponseEntity.ok(response);
    }

    public List<Customers> findAllByListOfCustomers(List<String> customerIds) {
        return customersRepository.findAllById(customerIds);
    }

    public ResponseEntity<?> removeProfilePicture() {

        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        String customerId = authentication.getName();
        Customers customers = customersRepository.findById(customerId).orElse(null);
        if (customers == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Utils.CUSTOMER_NOT_FOUND);
        }

        customers.setProfilePic(null);

        customersRepository.save(customers);

        return new ResponseEntity<>(Utils.PROFILE_PICTURE_REMOVED, HttpStatus.OK);
    }

    public ResponseEntity<?> changeMpin(CustomerMpinPayload payload) {

        String customerId = authentication.getName();

        Customers customer = customersRepository.findById(customerId).orElse(null);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Utils.CUSTOMER_NOT_FOUND);
        }

        String customerMobile = customer.getMobile();

        CustomerCredentials customerCredentials = customerCredentialsService
                .getCustomerCredentialsByMobile(customerMobile);
        if (customerCredentials == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Constants.CUSTOMER_CREDENTIALS_NOT_FOUND);
        }

        if (payload.mpin().equals(customerCredentials.getCustomerPin())){
            return new ResponseEntity<>(Constants.CHANGE_MPIN_CAN_NOT_BE_SAME, HttpStatus.BAD_REQUEST);
        }

        CustomersOtp customersOtp = customersOtpService.createNewCustomersOtp(customerCredentials);

        int otp = customersOtp.getOtp();

        if (!environment.equalsIgnoreCase(Utils.ENVIRONMENT_LOCAL)) {

            String otpMessage = "Dear user, your SmartStay change mpin OTP is " + otp +
                    ". Use this OTP to verify your new mpin. Do not share it with anyone. - SmartStay";

            otpService.sendOtp(customerCredentials.getCustomerMobile(), otpMessage);

            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(otp, HttpStatus.OK);
        }
    }

    public ResponseEntity<?> resendMpinOtp() {

        String customerId = authentication.getName();

        Customers customer = customersRepository.findById(customerId).orElse(null);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Utils.CUSTOMER_NOT_FOUND);
        }

        String customerMobile = customer.getMobile();

        CustomerCredentials customerCredentials = customerCredentialsService
                .getCustomerCredentialsByMobile(customerMobile);
        if (customerCredentials == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Constants.CUSTOMER_CREDENTIALS_NOT_FOUND);
        }

        Date now = new Date();
        Date expiryAt = new Date(now.getTime() + (15 * 60 * 1000));

        CustomersOtp customersOtp = customersOtpService
                .getByXuid(customerCredentials.getXuid());
        if (customersOtp == null) {
            return new ResponseEntity<>(Constants.OTP_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        if (!customersOtp.isVerified()) {
            customersOtp.setExpiryAt(expiryAt);
            customersOtp.setUpdatedAt(new Date());
            customersOtp = customersOtpService.save(customersOtp);
        } else {
            return new ResponseEntity<>(Constants.NO_UNVERIFIED_OTP, HttpStatus.FORBIDDEN);
        }

        int otp = customersOtp.getOtp();

        if (!environment.equalsIgnoreCase(Utils.ENVIRONMENT_LOCAL)) {

            String otpMessage = "Dear user, your SmartStay change mpin OTP is " + otp +
                    ". Use this OTP to verify your new mpin. Do not share it with anyone. - SmartStay";

            otpService.sendOtp(customerCredentials.getCustomerMobile(), otpMessage);

            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(otp, HttpStatus.OK);
        }
    }

    public ResponseEntity<?> verifyMpinOtp(CustomerMpinOtpPayload payload) {

        String customerId = authentication.getName();

        Customers customer = customersRepository.findById(customerId).orElse(null);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Utils.CUSTOMER_NOT_FOUND);
        }

        String customerMobile = customer.getMobile();

        CustomerCredentials customerCredentials = customerCredentialsService
                .getCustomerCredentialsByMobile(customerMobile);
        if (customerCredentials == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Constants.CUSTOMER_CREDENTIALS_NOT_FOUND);
        }

        CustomersOtp customersOtp = customersOtpService
                .getByXuid(customerCredentials.getXuid());
        if (customersOtp == null) {
            return new ResponseEntity<>(Constants.OTP_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        if (customersOtp.getOtp() == 0){
            return new ResponseEntity<>(Constants.OTP_NOT_GENERATED, HttpStatus.BAD_REQUEST);
        }

        if (customersOtp.getExpiryAt().before(new Date())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Constants.OTP_EXPIRED);
        }

        if (!String.valueOf(customersOtp.getOtp()).equals(payload.otp())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Constants.OTP_DOES_NOT_MATCH);
        }

        customersOtp.setVerified(true);
        customersOtp.setOtp(0);
        customersOtp.setUpdatedAt(new Date());
        customersOtp.setExpiryAt(null);

        customersOtpService.save(customersOtp);

        customerCredentials.setCustomerPin(payload.mpin());
        customerCredentials.setPinVerified(true);

        customerCredentials = customerCredentialsService.save(customerCredentials);

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("mobile", customerCredentials.getCustomerMobile());
        claims.put("mPin", customerCredentials.getCustomerPin());

        String token = jwtService.generateToken(customerId, claims);

        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    public ResponseEntity<?> raiseNoticeRequest(String hostelId, RaiseNoticePayload payload) {

        String customerId = authentication.getName();

        if (!existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        Customers customer = customersRepository.findById(customerId).orElse(null);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Utils.CUSTOMER_NOT_FOUND);
        }

        if (!CustomerStatus.CHECK_IN.name().equals(customer.getCurrentStatus())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Constants.CUSTOMER_NOT_CHECKED_IN);
        }

        BookingsV1 booking = bookingsService.getLatestBooking(customerId, hostelId);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Constants.BOOKING_NOT_FOUND);
        }

        if (raiseNoticeRequestService.existsPendingRequest(customerId, hostelId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Constants.REQUEST_ALREADY_EXISTS);
        }

        Date joiningDate = booking.getJoiningDate();
        Date requestDate = Utils.localDateToDate(payload.requestDate());
        Date checkoutDate = Utils.localDateToDate(payload.checkoutDate());

        if (Utils.compareWithTwoDates(requestDate, joiningDate) < 0) {
            return new ResponseEntity<>(Constants.REQUEST_DATE_MUST_AFTER_JOINING_DATE, HttpStatus.BAD_REQUEST);
        }

        if (Utils.compareWithTwoDates(checkoutDate, joiningDate) < 0) {
            return new ResponseEntity<>(Constants.CHECKOUT_DATE_MUST_AFTER_JOINING_DATE, HttpStatus.BAD_REQUEST);
        }

        Date today = new Date();

        RaiseNoticeRequest raiseNoticeRequest = new RaiseNoticeRequest();

        raiseNoticeRequest.setHostelId(hostelId);
        raiseNoticeRequest.setCustomerId(customerId);
        raiseNoticeRequest.setRequestedDate(requestDate);
        raiseNoticeRequest.setCheckoutDate(checkoutDate);
        raiseNoticeRequest.setReason(payload.reason());
        raiseNoticeRequest.setRequestStatus(RequestStatus.OPEN.name());
        raiseNoticeRequest.setCreatedAt(today);
        raiseNoticeRequest.setCreatedBy(customerId);
        raiseNoticeRequest.setActive(true);
        raiseNoticeRequest.setDeleted(false);

        raiseNoticeRequest = raiseNoticeRequestService.save(raiseNoticeRequest);

        notificationService.createRaiseNoticeRequestNotification(customerId,
                hostelId, raiseNoticeRequest.getId());
        fcmNotificationService.sendRaiseNoticeNotification(hostelId, customer);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}