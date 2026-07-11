package com.smartstay.tenant.service;

import com.smartstay.tenant.repository.CustomerNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerNotificationsService {

    @Autowired
    private CustomerNotificationRepository customerNotificationRepository;
}
