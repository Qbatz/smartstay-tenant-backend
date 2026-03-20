package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerDuplicateService {

    @Autowired
    private CustomerRepository customersRepository;

    public Customers getCustomerById(String customerId) {
        return customersRepository.findById(customerId).orElse(null);
    }
}
