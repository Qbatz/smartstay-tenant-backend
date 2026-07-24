package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.CustomerJobDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CustomerJobDetailsRepository extends JpaRepository<CustomerJobDetails, Long> {

    List<CustomerJobDetails> findAllByCustomerIdAndIsDeletedFalseOrderByJobIdDesc(String customerId);

    List<CustomerJobDetails> findAllByCustomerIdAndJobIdInAndIsDeletedFalseOrderByJobIdDesc(String customerId,
                                                                                            Set<Long> jobIds);
}
