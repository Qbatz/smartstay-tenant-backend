package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.UserConfig;
import com.smartstay.tenant.repository.UserConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserConfigService {

    @Autowired
    UserConfigRepository userConfigRepository;


     UserConfig getUserConfigByUserId(String userId) {
         return userConfigRepository.findByUserId(userId);
     }

     public UserConfig saveUserConfig(UserConfig userConfig) {
         return userConfigRepository.save(userConfig);
     }
}
