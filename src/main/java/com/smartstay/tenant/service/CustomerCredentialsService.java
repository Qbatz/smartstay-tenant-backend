package com.smartstay.tenant.service;


import com.smartstay.tenant.dao.CustomerCredentials;
import com.smartstay.tenant.repository.CustomerCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerCredentialsService {

    @Autowired
    private CustomerCredentialRepository repository;

    public CustomerCredentials getCustomerCredentialsByMobile(String mobileNo) {
        return repository.findByCustomerMobile(mobileNo);
    }

    public String getFCmToken(String customerId) {
        return repository.findFcmTokenByCustomerId(customerId);
    }

    public CustomerCredentials getCustomerCredentialsByXUuid(String xUuid) {
        return repository.findByXuid(xUuid);
    }

    public void saveCustomerCredentials(CustomerCredentials credentials) {
        repository.save(credentials);
    }
}
