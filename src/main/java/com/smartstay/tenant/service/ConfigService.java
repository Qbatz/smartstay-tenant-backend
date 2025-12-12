package com.smartstay.tenant.service;


import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.CustomerCredentials;
import com.smartstay.tenant.payload.login.UpdateFcm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {
    @Autowired
    CustomerCredentialsService customerCredentialsService;
    @Autowired
    Authentication authentication;


    public ResponseEntity<?> updateFcm(UpdateFcm updateFcm) {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.UNAUTHORIZED);
        }
        CustomerCredentials credentials = customerCredentialsService.getCustomerCredentialsByXUuid(updateFcm.xuid());
        if (credentials == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer not found.");
        }
        credentials.setFcmToken(updateFcm.fcmToken());
        customerCredentialsService.saveCustomerCredentials(credentials);
        return new ResponseEntity<>(Utils.UPDATED, HttpStatus.OK);
    }
}
