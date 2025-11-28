package com.smartstay.tenant.service;


import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.dao.CustomersOtp;
import com.smartstay.tenant.dao.UserConfig;
import com.smartstay.tenant.payload.login.TokenLogin;
import com.smartstay.tenant.payload.login.UpdateMpin;
import com.smartstay.tenant.repository.CustomerRepository;
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


    public ResponseEntity<?> updateMpin(UpdateMpin updateMpin) {
        Customers customers = customersRepository.findByCustomerId(updateMpin.userId());
        if (customers == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer not found.");
        }
        UserConfig userConfig = userConfigService.getUserConfigByUserId(updateMpin.userId());
        if (userConfig == null) {
            userConfig = new UserConfig();
            userConfig.setUserId(updateMpin.userId());
            userConfig.setMPin(updateMpin.newMpin());
        }else  {
            userConfig.setMPin(updateMpin.newMpin());
        }
        userConfigService.saveUserConfig(userConfig);
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("mobile", customers.getMobile());
        claims.put("mPin", updateMpin.newMpin());
        String token = jwtService.generateToken(customers.getCustomerId(), claims);
        return new ResponseEntity<>(
                token
                , HttpStatus.OK);

    }


    public ResponseEntity<?> verifyMpin(UpdateMpin updateMpin) {
        Customers customers = customersRepository.findByCustomerId(updateMpin.userId());
        if (customers == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found. Please register first.");
        }
        UserConfig userConfig = userConfigService.getUserConfigByUserId(updateMpin.userId());
        if (userConfig == null || userConfig.getMPin() == null || !userConfig.getMPin().equals(updateMpin.newMpin())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Your MPin is incorrect. Please try again.");
        }
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("mobile", customers.getMobile());
        claims.put("mPin", updateMpin.newMpin());
        String token = jwtService.generateToken(customers.getCustomerId(), claims);
        return new ResponseEntity<>(
                token
                , HttpStatus.OK);
    }


}
