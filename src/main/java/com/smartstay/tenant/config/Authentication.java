package com.smartstay.tenant.config;

import io.jsonwebtoken.Claims;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class Authentication {

    public boolean isAuthenticated() {
        org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.isAuthenticated();
    }


    public String getName() {
        org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    public Claims getDetails() {
        org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()) {
            return (Claims) authentication.getDetails();
        }

        return null;
    }
}
