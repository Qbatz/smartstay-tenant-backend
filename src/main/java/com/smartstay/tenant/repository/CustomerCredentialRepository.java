package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.CustomerCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerCredentialRepository extends JpaRepository<CustomerCredentials, String> {
    CustomerCredentials findByCustomerMobile(String mobileNo);
    CustomerCredentials findByXuid(String xuid);
    @Query("""
        SELECT cc.fcmToken
        FROM CustomerCredentials cc
        JOIN Customers c ON c.xuid = cc.xuid
        WHERE c.customerId = :customerId
    """)
    String findFcmTokenByCustomerId(@Param("customerId") String customerId);
}
