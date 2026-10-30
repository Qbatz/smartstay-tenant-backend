package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.Credentials;
import com.smartstay.tenant.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CredentialsService {

    @Autowired
    private CredentialsRepository credentialsRepository;

    public Credentials getByService(String service){
        return credentialsRepository.findByService(service);
    }
}
