package com.smartstay.tenant.service;


import com.smartstay.tenant.dao.CustomerCredentials;
import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.dao.CustomersOtp;
import com.smartstay.tenant.dao.UserConfig;
import com.smartstay.tenant.payload.login.Login;
import com.smartstay.tenant.payload.login.TokenLogin;
import com.smartstay.tenant.payload.login.VerifyOtp;
import com.smartstay.tenant.repository.CustomerRepository;
import com.smartstay.tenant.repository.CustomersOtpRepository;
import com.smartstay.tenant.response.VerifyOtpResponse;
import com.smartstay.tenant.response.login.VerifyMobileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class UserService {

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JWTService jwtService;

    @Autowired
    CustomerRepository customersRepository;

    @Autowired
    CustomersOtpRepository customersOtpRepository;


    @Autowired
    UserConfigService userConfigService;

    @Autowired
    CustomerCredentialsService customerCredentialsService;

    public CustomersOtp getOtpByMobile(String xUuid) {
        return customersOtpRepository.findByXuid(xUuid);
    }

    public ResponseEntity<?> login(Login login) {
        CustomerCredentials credentials = customerCredentialsService.getCustomerCredentialsByMobile(login.mobile());
        if (credentials != null) {
            CustomersOtp customersOtp = getOtpByMobile(credentials.getXuid());
            Date now = new Date();
            Date expiryAt = new Date(now.getTime() + (15 * 60 * 1000));
            if (customersOtp != null) {
                customersOtp.setOtp(generateSixDigitOtp());
                customersOtp.setExpiryAt(expiryAt);
                customersOtp.setUpdatedAt(new Date());
                customersOtp.setVerified(false);
                customersOtpRepository.save(customersOtp);

            } else {
                customersOtp = new CustomersOtp();
                customersOtp.setXuid(credentials.getXuid());
                customersOtp.setOtp(generateSixDigitOtp());
                customersOtp.setExpiryAt(expiryAt);
                customersOtp.setCreatedAt(new Date());
                customersOtp.setVerified(false);
                customersOtpRepository.save(customersOtp);
            }

            return new ResponseEntity<>(new VerifyMobileResponse(
                    credentials.getXuid(), customersOtp.getOtp()
            ), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }


    public ResponseEntity<?> resendOtp(String xuid) {
        CustomerCredentials credentials = customerCredentialsService.getCustomerCredentialsByXUuid(xuid);
        if (credentials != null) {
            CustomersOtp customersOtp = getOtpByMobile(credentials.getXuid());
            Date now = new Date();
            Date expiryAt = new Date(now.getTime() + (15 * 60 * 1000));
            if (customersOtp != null && !customersOtp.isVerified()) {
                customersOtp.setExpiryAt(expiryAt);
                customersOtp.setUpdatedAt(new Date());
                customersOtpRepository.save(customersOtp);

            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            return new ResponseEntity<>(customersOtp.getOtp(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

    public ResponseEntity<?> verifyOtp(VerifyOtp verifyOtp) {
        CustomerCredentials credentials = customerCredentialsService.getCustomerCredentialsByMobile(verifyOtp.mobileNo());
        if (credentials == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found. Please register first.");
        }

        CustomersOtp customersOtp = getOtpByMobile(credentials.getXuid());
        if (customersOtp == null || customersOtp.getOtp() == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No OTP generated for this mobile number. Please request a new OTP.");
        }

        if (customersOtp.getExpiryAt().before(new Date())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OTP has expired. Please request a new one.");
        }

        if (customersOtp.getOtp() != verifyOtp.otp()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP. Please check and try again.");
        }
        customersOtp.setVerified(true);
        customersOtp.setOtp(0);
        customersOtp.setUpdatedAt(new Date());
        customersOtp.setExpiryAt(null);
        customersOtpRepository.save(customersOtp);
        return new ResponseEntity<>(
                new VerifyOtpResponse(credentials.getXuid(), credentials.isPinVerified())
                , HttpStatus.OK);
    }

    private int generateSixDigitOtp() {
        return (int) (100000 + Math.random() * 900000);
    }
}
