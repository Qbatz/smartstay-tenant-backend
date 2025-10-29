package com.smartstay.tenant.config;

import com.smartstay.tenant.dao.Customers;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrinciple implements UserDetails {

    private Customers customers;

    public UserPrinciple(Customers customers) {
        this.customers = customers;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(String.valueOf(customers.getMobile())));
    }

    @Override
    public String getPassword() {
        return customers.getMobSerialNo();
    }

    @Override
    public String getUsername() {
        return customers.getMobile();
    }

    public Customers getCustomers() {
        return customers;
    }
}
