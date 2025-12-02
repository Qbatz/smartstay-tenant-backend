package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.CustomerCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerCredentialRepository extends JpaRepository<CustomerCredentials, String> {
    CustomerCredentials findByCustomerMobile(String mobileNo);
    CustomerCredentials findByXuid(String xuid);
}
