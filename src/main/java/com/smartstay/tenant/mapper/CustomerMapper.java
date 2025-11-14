package com.smartstay.tenant.mapper;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.response.customer.CustomerDetails;
import com.smartstay.tenant.response.customer.CustomerKycDetails;


public class CustomerMapper {

    public CustomerDetails toDetailsDto(Customers c) {
        return new CustomerDetails(c.getCustomerId(), c.getFirstName(), c.getLastName(), c.getHouseNo(), c.getStreet(), c.getLandmark(), c.getPincode(), c.getCity(), c.getState(), c.getProfilePic(),
                Utils.dateToString(c.getJoiningDate()),
                new CustomerKycDetails(
                        c.getKycDetails() != null ? c.getKycDetails().getCurrentStatus() : null,
                        c.getKycDetails() != null ? c.getKycDetails().getTransactionId() : null,
                        c.getKycDetails() != null ? c.getKycDetails().getReferenceId() : null
                ));
    }
}