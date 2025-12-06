package com.smartstay.tenant.service;


import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.CustomerCredentials;
import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.payload.login.*;
import com.smartstay.tenant.repository.CustomerRepository;
import com.smartstay.tenant.repository.HostelRepository;
import com.smartstay.tenant.response.customer.CustomerHostels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class LoginService {


    @Autowired
    CustomerRepository customersRepository;
    @Autowired
    Authentication authentication;
    @Autowired
    JWTService jwtService;
    @Autowired
    UserConfigService userConfigService;
    @Autowired
    CustomerCredentialsService customerCredentialsService;
    @Autowired
    private HostelRepository hostelRepository;

    public ResponseEntity<?> updateMpin(UpdateMpin updateMpin) {
        CustomerCredentials credentials = customerCredentialsService.getCustomerCredentialsByXUuid(updateMpin.xuid());
        if (credentials == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer not found.");
        }
        credentials.setPinVerified(true);
        credentials.setCustomerPin(updateMpin.mPin());
        customerCredentialsService.saveCustomerCredentials(credentials);
        List<CustomerHostels> customerHostels = getHostels(credentials.getCustomerMobile());
        return new ResponseEntity<>(customerHostels, HttpStatus.OK);
    }

    public ResponseEntity<?> getHostelsList(String xuid) {
//        if (!authentication.isAuthenticated()){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.UNAUTHORIZED);
//        }
        CustomerCredentials credentials = customerCredentialsService.getCustomerCredentialsByXUuid(xuid);
        if (credentials == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer not found.");
        }
        List<CustomerHostels> customerHostels = getHostels(credentials.getCustomerMobile());
        return new ResponseEntity<>(customerHostels, HttpStatus.OK);
    }




    public ResponseEntity<?> requestToken(RequestToken requestToken) {
        CustomerCredentials credentials = customerCredentialsService.getCustomerCredentialsByXUuid(requestToken.xuid());
        if (credentials == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer not found.");
        }
        Customers customers = customersRepository.findByMobileAndHostelId(credentials.getCustomerMobile(), requestToken.hostelId());
        if (customers == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer not found for the specified hostel. Please register first.");
        }
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("mobile", credentials.getCustomerMobile());
        claims.put("mPin", credentials.getCustomerPin());
        String token = jwtService.generateToken(customers.getCustomerId(), claims);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    public ResponseEntity<?> verifyMpin(VerifyMpin verifyMpin) {
        CustomerCredentials credentials = customerCredentialsService.getCustomerCredentialsByXUuid(verifyMpin.xuid());
        if (credentials == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer not found. Please register first.");
        }
        if (!credentials.getCustomerPin().equals(verifyMpin.mPin())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid M-Pin. Please try again.");
        }
        List<CustomerHostels> customerHostels = getHostels(credentials.getCustomerMobile());
        return new ResponseEntity<>(customerHostels, HttpStatus.OK);
    }

    public ResponseEntity<?> logOut(LogOut logOut) {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.UNAUTHORIZED);
        }
        CustomerCredentials credentials = customerCredentialsService.getCustomerCredentialsByXUuid(logOut.xuid());
        if (credentials == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer not found.");
        }
        credentials.setFcmToken("");
        customerCredentialsService.saveCustomerCredentials(credentials);
        return new ResponseEntity<>(Utils.UPDATED, HttpStatus.OK);
    }

    public List<CustomerHostels> getHostels(String mobileNo) {
        return hostelRepository.findHostelsByMobile(mobileNo);

    }




}
