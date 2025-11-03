package com.smartstay.tenant.mapper;

import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.response.CustomerDetails;


public class CustomerMapper {

    public CustomerDetails toDetailsDto(Customers c) {
        return new CustomerDetails(c.getCustomerId(), c.getFirstName(), c.getLastName(), c.getHouseNo(), c.getStreet(), c.getLandmark(), c.getPincode(), c.getCity(), c.getState());
    }
}