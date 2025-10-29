package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.CustomersOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomersOtpRepository extends JpaRepository<CustomersOtp, String> {
    CustomersOtp findByCustomerId(String customerId);
}
