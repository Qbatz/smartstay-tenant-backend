package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.CustomerDocuments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerDocumentRepository extends JpaRepository<CustomerDocuments, Long> {
    List<CustomerDocuments> findAllByCustomerIdAndIsDeletedFalseAndIsActiveTrueOrderByDocumentIdDesc(String customerId);
}
