package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.config.FilesConfig;
import com.smartstay.tenant.config.UploadFileToS3;
import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.ennum.Gender;
import com.smartstay.tenant.mapper.CustomerMapper;
import com.smartstay.tenant.repository.CustomerRepository;
import com.smartstay.tenant.response.customer.EditCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    CustomerRepository customersRepository;
    @Autowired
    UserHostelService userHostelService;
    @Autowired
    Authentication authentication;

    @Autowired
    private BookingsService bookingsService;
    @Autowired
    private UploadFileToS3 uploadToS3;

    public ResponseEntity<?> getCustomerDetails() {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Utils.UNAUTHORIZED);
        }
        String customerId = authentication.getName();
        Customers customers = customersRepository.findById(customerId).orElse(null);
        if (customers == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.CUSTOMER_NOT_FOUND);
        }
        return new ResponseEntity<>(new CustomerMapper().toDetailsDto(customers, bookingsService.getCustomerBookingDetails(customerId)), HttpStatus.OK);

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
                profileImage = uploadToS3.uploadFileToS3(FilesConfig.convertMultipartToFileNew(file), "customer/profile");
                customers.setProfilePic(profileImage);
            }

            if (updateInfo.firstName() != null && !updateInfo.firstName().equalsIgnoreCase("")) {
                customers.setFirstName(updateInfo.firstName());
            }
            if (updateInfo.lastName() != null && !updateInfo.lastName().equalsIgnoreCase("")) {
                customers.setLastName(updateInfo.lastName());
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
                Gender gender = Gender.valueOf(updateInfo.gender().toUpperCase());
                customers.setGender(gender.getLabel());
            }

            customersRepository.save(customers);

            return new ResponseEntity<>(Utils.UPDATED, HttpStatus.OK);

        } else {
            return new ResponseEntity<>(Utils.PAYLOADS_REQUIRED, HttpStatus.BAD_REQUEST);
        }
    }


}
