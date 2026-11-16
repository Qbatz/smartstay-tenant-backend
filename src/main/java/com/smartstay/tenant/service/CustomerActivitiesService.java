package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.CustomerActivitiesUtils;
import com.smartstay.tenant.dao.CustomerActivities;
import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.ennum.CustomerActivitySourceEnum;
import com.smartstay.tenant.ennum.CustomerActivityTypeEnum;
import com.smartstay.tenant.repository.CustomerActivitiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CustomerActivitiesService {

    @Autowired
    private CustomerActivitiesRepository customerActivitiesRepository;

    public void createCustomerActivity(Customers loggedInCustomer, String tenantPlatform,
                                       CustomerActivityTypeEnum customerActivityType,
                                       CustomerActivitySourceEnum customerActivitySource,
                                       String sourceId, String userType, List<String> sourceIds) {

        if (loggedInCustomer == null || customerActivityType == null
                || customerActivitySource == null) {
            return;
        }

        Date today = new Date();

        CustomerActivities customerActivities = new CustomerActivities();

        customerActivities.setCustomerId(loggedInCustomer.getCustomerId());
        customerActivities.setHostelId(loggedInCustomer.getHostelId());
        customerActivities.setActivityType(customerActivityType.name());
        customerActivities.setDescription(CustomerActivitiesUtils
                .buildDescription(loggedInCustomer, customerActivityType, customerActivitySource));
        customerActivities.setSource(customerActivitySource.name());
        customerActivities.setSourceId(sourceId);
        customerActivities.setUserType(userType);
        customerActivities.setCreatedAt(today);
        customerActivities.setPlatform(tenantPlatform);
        customerActivities.setSourceIds(sourceIds);

        customerActivitiesRepository.save(customerActivities);
    }

    public void createCustomerActivity(Customers loggedInCustomer, String tenantPlatform,
                                       CustomerActivityTypeEnum customerActivityType,
                                       CustomerActivitySourceEnum customerActivitySource,
                                       String sourceId, Date createdAt, String userType,
                                       List<String> sourceIds) {

        if (loggedInCustomer == null || customerActivityType == null
                || customerActivitySource == null) {
            return;
        }

        CustomerActivities customerActivities = new CustomerActivities();

        customerActivities.setCustomerId(loggedInCustomer.getCustomerId());
        customerActivities.setHostelId(loggedInCustomer.getHostelId());
        customerActivities.setActivityType(customerActivityType.name());
        customerActivities.setDescription(CustomerActivitiesUtils
                .buildDescription(loggedInCustomer, customerActivityType, customerActivitySource));
        customerActivities.setSource(customerActivitySource.name());
        customerActivities.setSourceId(sourceId);
        customerActivities.setUserType(userType);
        customerActivities.setCreatedAt(createdAt);
        customerActivities.setPlatform(tenantPlatform);
        customerActivities.setSourceIds(sourceIds);

        customerActivitiesRepository.save(customerActivities);
    }
}
