package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.CustomerCredentials;
import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.ennum.CustomerActivitySourceEnum;
import com.smartstay.tenant.ennum.CustomerActivityTypeEnum;
import com.smartstay.tenant.ennum.UserType;
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
    @Autowired
    private CustomerActivitiesService customerActivitiesService;
    @Autowired
    private CustomerService customerService;

    public ResponseEntity<?> updateFcm(UpdateFcm updateFcm) {

        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.UNAUTHORIZED);
        }

        String customerId = authentication.getName();
        String tenantPlatform = authentication.getPlatform();

        Customers customer = customerService.getCustomerInformation(customerId);
        if (customer == null){
            return new ResponseEntity<>(Utils.CUSTOMER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        CustomerCredentials credentials = customerCredentialsService
                .getCustomerCredentialsByXUuid(updateFcm.xuid());
        if (credentials == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer not found.");
        }

        credentials.setFcmToken(updateFcm.fcmToken());
        credentials.setTenantPlatform(tenantPlatform);

        credentials = customerCredentialsService.saveCustomerCredentials(credentials);

        customerActivitiesService.createCustomerActivity(customer, tenantPlatform,
                CustomerActivityTypeEnum.UPDATE, CustomerActivitySourceEnum.CUSTOMER_CREDENTIALS,
                credentials.getXuid(), UserType.ALL_EXCEPT_TENANT.name(), null);

        return new ResponseEntity<>(Utils.UPDATED, HttpStatus.OK);
    }
}
