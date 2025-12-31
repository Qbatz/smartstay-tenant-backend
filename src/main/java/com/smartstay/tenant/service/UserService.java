package com.smartstay.tenant.service;


import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dao.*;
import com.smartstay.tenant.payload.login.Login;
import com.smartstay.tenant.payload.login.TokenLogin;
import com.smartstay.tenant.payload.login.VerifyOtp;
import com.smartstay.tenant.repository.CustomerRepository;
import com.smartstay.tenant.repository.CustomersOtpRepository;
import com.smartstay.tenant.repository.UserRepository;
import com.smartstay.tenant.response.VerifyOtpResponse;
import com.smartstay.tenant.response.login.VerifyMobileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
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
    UserRepository userRepository;

    @Autowired
    CustomerCredentialsService customerCredentialsService;

    @Autowired
    OtpService otpService;

    @Value("${ENVIRONMENT}")
    private String environment;

    public CustomersOtp getOtpByMobile(String xUuid) {
        return customersOtpRepository.findByXuid(xUuid);
    }

    public ResponseEntity<?> login(Login login) {
        CustomerCredentials credentials = customerCredentialsService.getCustomerCredentialsByMobile(login.mobile());
        if (credentials != null) {
            CustomersOtp customersOtp = getOtpByMobile(credentials.getXuid());
            Date now = new Date();
            Date expiryAt = new Date(now.getTime() + (15 * 60 * 1000));
            int otp = generateSixDigitOtp();
            if (customersOtp != null) {
                customersOtp.setOtp(otp);
                customersOtp.setExpiryAt(expiryAt);
                customersOtp.setUpdatedAt(new Date());
                customersOtp.setVerified(false);
                customersOtpRepository.save(customersOtp);

            } else {
                customersOtp = new CustomersOtp();
                customersOtp.setXuid(credentials.getXuid());
                customersOtp.setOtp(otp);
                customersOtp.setExpiryAt(expiryAt);
                customersOtp.setCreatedAt(new Date());
                customersOtp.setVerified(false);
                customersOtpRepository.save(customersOtp);
            }

            if (!environment.equalsIgnoreCase(Utils.ENVIRONMENT_LOCAL)) {
                String otpMessage = "Dear user, your SmartStay Login OTP is " + otp +
                        ". Use this OTP to verify your login. Do not share it with anyone. - SmartStay";
                otpService.sendOtp(credentials.getCustomerMobile(), otpMessage);

                return new ResponseEntity<>(credentials.getXuid(), HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new VerifyMobileResponse(
                        credentials.getXuid(), customersOtp.getOtp()
                ), HttpStatus.OK);
            }



        } else {
            return new ResponseEntity<>("You are not belongs to any hostels.", HttpStatus.BAD_REQUEST);
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

            if (!environment.equalsIgnoreCase(Utils.ENVIRONMENT_LOCAL)) {
                String otpMessage = "Dear user, your SmartStay Login OTP is " + customersOtp.getOtp() +
                        ". Use this OTP to verify your login. Do not share it with anyone. - SmartStay";
                otpService.sendOtp(credentials.getCustomerMobile(), otpMessage);


                return new ResponseEntity<>("Otp sent to your mobile number", HttpStatus.OK);
            }else {
                return new ResponseEntity<>(customersOtp.getOtp(), HttpStatus.OK);
            }

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


    public Users findUserByUserId(String userId) {
        return userRepository.findUserByUserId(userId);
    }

}
