package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.dao.HostelV1;
import com.smartstay.tenant.mapper.CustomerMapper;
import com.smartstay.tenant.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {


    @Autowired
    CustomerRepository customersRepository;

    @Autowired
    Authentication authentication;


    public ResponseEntity<?> getCustomerDetails() {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Utils.UNAUTHORIZED);
        }
        String customerId = authentication.getName();
        Customers customers = customersRepository.findById(customerId).orElse(null);
        if (customers == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Utils.CUSTOMER_NOT_FOUND);
        }
        return new ResponseEntity<>(new CustomerMapper().toDetailsDto(customers), HttpStatus.OK);

    }

    HostelV1 findByCustomerIdAndHostelId(String customerId, String hostelId){
        return customersRepository.findByCustomerIdAndHostelId(customerId, hostelId);
    }


    boolean existsByCustomerIdAndHostelId(String customerId, String hostelId){
        return customersRepository.existsByCustomerIdAndHostelId(customerId, hostelId);
    }

    Customers getCustomerById(String customerId){
        return customersRepository.findById(customerId).orElse(null);
    }

    boolean existsByHostelIdAndCustomerIdAndStatusesIn(String hostelId, String customerId, List<String> statuses){
        return customersRepository.existsByHostelIdAndCustomerIdAndStatusesIn(hostelId, customerId, statuses);
    }


}
