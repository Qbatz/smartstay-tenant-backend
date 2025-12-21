package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.CustomersBedHistory;
import com.smartstay.tenant.repository.CustomerBedHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CustomerBedHistoryService {


    @Autowired
    private CustomerBedHistoryRepository customerBedHistoryRepository;


    public CustomersBedHistory getCustomerBedByStartDate(String customerId, Date startDate, Date endDate) {
        CustomersBedHistory cbh = customerBedHistoryRepository.findByCustomerIdAndDate(customerId, startDate, endDate);
        return cbh;
    }

    public CustomersBedHistory getCustomerBookedBed(String customerId) {
        return customerBedHistoryRepository.findByCustomerIdAndTypeBooking(customerId);
    }
}
