package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.CustomerDocuments;
import com.smartstay.tenant.repository.CustomerDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerDocumentService {
    
    @Autowired
    CustomerDocumentRepository customerDocumentRepository;

    public List<CustomerDocuments> getDocumentsByCustomerId(String customerId) {
        return customerDocumentRepository
                .findAllByCustomerIdAndIsDeletedFalseAndIsActiveTrueOrderByDocumentIdDesc(customerId);
    }
}
