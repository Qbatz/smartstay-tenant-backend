package com.smartstay.tenant.service;


import com.smartstay.tenant.config.UserPrinciple;
import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String customerId) throws UsernameNotFoundException {
        List<Customers> customers = customerRepository.findByCustomerId(customerId);

        if (customers == null || customers.isEmpty()) {
            return null;
        }
        return new UserPrinciple(customers.getFirst());
    }
}
