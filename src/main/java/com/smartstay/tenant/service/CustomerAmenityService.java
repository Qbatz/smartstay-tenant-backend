package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.CustomersAmenity;
import com.smartstay.tenant.repository.CustomerAmenityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class CustomerAmenityService {

    @Autowired
    private CustomerAmenityRepository customerAmenityRepository;

    public CustomersAmenity findLatestWithEndDateNullByAmenityIdAndCustomerId(String amenityId, String customerId) {
        return customerAmenityRepository
                .findTopByAmenityIdAndCustomerIdAndEndDateIsNullOrderByCreatedAtDesc(amenityId, customerId);
    }

    public CustomersAmenity findLatestWithEndDateByAmenityIdAndCustomerId(String amenityId, String customerId,
                                                                          Date date) {
        return customerAmenityRepository
                .findByEndDateAndAmenityIdAndCustomerId(date, amenityId, customerId);
    }

    public List<CustomersAmenity> getLatestWithEndDateByCustomerIdAndAmenityIds(Date today, String customerId,
                                                                                Set<String> amenityIds) {
        return customerAmenityRepository
                .findLatestByEndDateAndCustomerIdAndAmenityIdIn(today, customerId, amenityIds);
    }
}
