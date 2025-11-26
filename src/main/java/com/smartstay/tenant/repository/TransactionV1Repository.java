package com.smartstay.tenant.repository;


import com.smartstay.tenant.dao.TransactionV1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionV1Repository extends JpaRepository<TransactionV1, String> {

    List<TransactionV1> findByCustomerIdAndHostelId(String customerId,String hostelId);

}
