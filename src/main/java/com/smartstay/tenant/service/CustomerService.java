package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.dao.CustomersOtp;
import com.smartstay.tenant.payload.login.Login;
import com.smartstay.tenant.payload.login.TokenLogin;
import com.smartstay.tenant.payload.login.VerifyOtp;
import com.smartstay.tenant.repository.CustomerRepository;
import com.smartstay.tenant.repository.CustomersOtpRepository;
import com.smartstay.tenant.response.VerifyOtpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JWTService jwtService;

    @Autowired
    CustomerRepository customersRepository;

    @Autowired
    CustomersOtpRepository customersOtpRepository;

    public CustomersOtp getOtpByMobile(String customerId) {
        return customersOtpRepository.findByCustomerId(customerId);
    }

    public ResponseEntity<?> login(Login login) {
        List<Customers> customers = customersRepository.findByMobile(login.mobile());
        if (customers != null && !customers.isEmpty()) {
            CustomersOtp customersOtp = getOtpByMobile(customers.getFirst().getCustomerId());
            Date now = new Date();
            Date expiryAt = new Date(now.getTime() + (15 * 60 * 1000));
            if (customersOtp != null) {
                customersOtp.setOtp(generateSixDigitOtp());
                customersOtp.setExpiryAt(expiryAt);
                customersOtp.setUpdatedAt(new Date());
                customersOtpRepository.save(customersOtp);

            } else {
                customersOtp = new CustomersOtp();
                customersOtp.setCustomerId(customers.getFirst().getCustomerId());
                customersOtp.setOtp(generateSixDigitOtp());
                customersOtp.setExpiryAt(expiryAt);
                customersOtp.setCreatedAt(new Date());
                customersOtpRepository.save(customersOtp);
            }

            return new ResponseEntity<>(customersOtp.getOtp(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

    public ResponseEntity<?> verifyOtp(VerifyOtp verifyOtp) {
        List<Customers> customers = customersRepository.findByMobile(verifyOtp.mobileNo());
        if (customers == null || customers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found. Please register first.");
        }

        CustomersOtp customersOtp = getOtpByMobile(customers.getFirst().getCustomerId());
        if (customersOtp == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No OTP generated for this mobile number. Please request a new OTP.");
        }

        if (customersOtp.getExpiryAt().before(new Date())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OTP has expired. Please request a new one.");
        }

        if (customersOtp.getOtp() != verifyOtp.otp()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP. Please check and try again.");
        }

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("mobile", customers.getFirst().getMobile());
        claims.put("email", customers.getFirst().getEmailId());
        claims.put("serialNo", verifyOtp.serialNo());
        claims.put("customerId", customers.getFirst().getCustomerId());
        String token = jwtService.generateToken(customers.getFirst().getMobile(), claims);
        customersOtpRepository.delete(customersOtp);
        Customers customers1 = customers.getFirst();
        customers1.setMobSerialNo(verifyOtp.serialNo());
        customersRepository.save(customers1);
        return new ResponseEntity<>(new VerifyOtpResponse(
                customers.getFirst().getCustomerId(),
                token
        ), HttpStatus.OK);
    }

    public ResponseEntity<?> tokenLogin(TokenLogin tokenLogin) {
        List<Customers> customers = customersRepository.findByMobile(tokenLogin.mobileNo());
        if (customers == null || customers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found. Please register first.");
        }
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("mobile", customers.getFirst().getMobile());
        claims.put("email", customers.getFirst().getEmailId());
        claims.put("serialNo", tokenLogin.serialNo());
        claims.put("customerId", customers.getFirst().getCustomerId());
        String token = jwtService.generateToken(customers.getFirst().getMobile(), claims);
        Customers customers1 = customers.getFirst();
        customers1.setMobSerialNo(tokenLogin.serialNo());
        customersRepository.save(customers1);
        return new ResponseEntity<>(new VerifyOtpResponse(
                customers.getFirst().getCustomerId(),
                token
        ), HttpStatus.OK);
    }




    private int generateSixDigitOtp() {
        return (int) (100000 + Math.random() * 900000);
    }


}
