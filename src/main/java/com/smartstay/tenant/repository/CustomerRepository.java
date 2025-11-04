package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.Customers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customers, String> {

    List<Customers> findByMobile(String customerId);
    List<Customers> findByCustomerId(String customerId);

}
