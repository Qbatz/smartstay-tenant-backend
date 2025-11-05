package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.repository.HostelRepository;
import com.smartstay.tenant.response.customer.CustomerHostels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HostelService {


    @Autowired
    private HostelRepository hostelRepository;

    @Autowired
    private Authentication authentication;

    public ResponseEntity<?> getHostels() {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Utils.UNAUTHORIZED);
        }
        String customerId = authentication.getName();
        List<CustomerHostels> hostels = hostelRepository.findHostels(customerId);
        return new ResponseEntity<>(hostels, HttpStatus.OK);

    }

    public ResponseEntity<?> getHostelDetails(String hostelId) {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Utils.UNAUTHORIZED);
        }
        String customerId = authentication.getName();
        List<CustomerHostels> hostels = hostelRepository.findHostels(customerId);
        return new ResponseEntity<>(hostels, HttpStatus.OK);

    }



}
