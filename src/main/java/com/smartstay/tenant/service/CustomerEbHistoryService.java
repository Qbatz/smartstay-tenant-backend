package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.CustomersEbHistory;
import com.smartstay.tenant.repository.CustomerEbHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CustomerEbHistoryService {

    @Autowired
    private CustomerEbHistoryRepository customerEbHistoryRepository;

    public List<CustomersEbHistory> getAllByCustomerIdAndDateBetween(String customerId, Date startDate, Date endDate) {
        return customerEbHistoryRepository.getAllByCustomerIdAndDateBetween(customerId, startDate, endDate);
    }
}
