package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Constants;
import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.CustomerJobDetails;
import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.ennum.UserType;
import com.smartstay.tenant.payload.customer.CustomerJobDetailsIdPayload;
import com.smartstay.tenant.payload.customer.CustomerJobDetailsPayload;
import com.smartstay.tenant.repository.CustomerJobDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CustomerJobDetailsService {

    @Autowired
    private CustomerJobDetailsRepository customerJobDetailsRepository;
    @Autowired
    private Authentication authentication;
    @Autowired
    @Lazy
    private CustomerService customerService;

    public ResponseEntity<?> createOrUpdateCustomerJobDetails(List<CustomerJobDetailsPayload> payloads) {

        String customerId = authentication.getName();
        Customers customer = customerService.getCustomerById(customerId);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.CUSTOMER_NOT_FOUND);
        }

        List<CustomerJobDetails> customerJobDetailsList = customerJobDetailsRepository
                .findAllByCustomerIdAndIsDeletedFalseOrderByJobIdDesc(customerId);

        Map<Long, CustomerJobDetails> customerJobDetailsMap = customerJobDetailsList.stream()
                .collect(Collectors.toMap(CustomerJobDetails::getJobId,
                        Function.identity(), (a, b) -> a));

        Date today = new Date();

        List<CustomerJobDetails> updatableCustomerJobDetails = new ArrayList<>();

        for (CustomerJobDetailsPayload payload : payloads) {

            if (payload == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.PAYLOADS_REQUIRED);
            }

            CustomerJobDetails customerJobDetails = customerJobDetailsMap
                    .getOrDefault(payload.jobId(), null);

            if (payload.jobId() != null && customerJobDetails == null) {
                return ResponseEntity.badRequest().body(Constants.INVALID_JOB_ID);
            }

            boolean hasData = false;
            boolean isNew = false;

            if (customerJobDetails == null) {
                isNew = true;
                customerJobDetails = new CustomerJobDetails();
            }

            if (payload.employmentStatus() != null) {
                hasData = true;
                customerJobDetails.setEmploymentStatus(payload.employmentStatus().trim());
            }
            if (payload.organizationName() != null) {
                hasData = true;
                customerJobDetails.setOrganizationName(payload.organizationName().trim());
            }
            if (payload.role() != null) {
                hasData = true;
                customerJobDetails.setRole(payload.role().trim());
            }
            if (payload.workLocation() != null) {
                hasData = true;
                customerJobDetails.setWorkLocation(payload.workLocation().trim());
            }
            if (payload.shiftType() != null) {
                hasData = true;
                customerJobDetails.setShiftType(payload.shiftType().trim());
            }
            if (payload.shiftFrom() != null) {
                hasData = true;
                customerJobDetails.setShiftStartTime(payload.shiftFrom().trim());
            }
            if (payload.shiftTo() != null) {
                hasData = true;
                customerJobDetails.setShiftEndTime(payload.shiftTo().trim());
            }

            if (hasData) {
                if (isNew) {
                    customerJobDetails.setCustomerId(customerId);
                    customerJobDetails.setHostelId(customer.getHostelId());
                    customerJobDetails.setIsDeleted(false);
                    customerJobDetails.setCreatedByUserType(UserType.TENANT.name());
                    customerJobDetails.setCreatedBy(customerId);
                    customerJobDetails.setCreatedAt(today);
                } else {
                    customerJobDetails.setUpdatedByUserType(UserType.TENANT.name());
                    customerJobDetails.setUpdatedBy(customerId);
                    customerJobDetails.setUpdatedAt(today);
                }
                updatableCustomerJobDetails.add(customerJobDetails);
            }
        }

        if (!updatableCustomerJobDetails.isEmpty()) {
            customerJobDetailsRepository.saveAll(updatableCustomerJobDetails);
        }

        return ResponseEntity.ok(Utils.SUCCESS);
    }

    public ResponseEntity<?> deleteCustomerDetails(List<CustomerJobDetailsIdPayload> payloads) {

        String customerId = authentication.getName();
        Customers customer = customerService.getCustomerById(customerId);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.CUSTOMER_NOT_FOUND);
        }

        Set<Long> jobIds = payloads.stream()
                .filter(p -> p != null && p.jobId() != null)
                .map(CustomerJobDetailsIdPayload::jobId)
                .collect(Collectors.toSet());

        List<CustomerJobDetails> customerJobDetailsList = customerJobDetailsRepository
                .findAllByCustomerIdAndJobIdInAndIsDeletedFalseOrderByJobIdDesc(customerId, jobIds);

        Map<Long, CustomerJobDetails> customerJobDetailsMap = customerJobDetailsList.stream()
                .collect(Collectors.toMap(CustomerJobDetails::getJobId,
                        Function.identity(), (a, b) -> a));

        Date today = new Date();

        List<CustomerJobDetails> deletableCustomerJobDetails = new ArrayList<>();

        for (CustomerJobDetailsIdPayload payload : payloads) {

            if (payload == null || payload.jobId() == null) {
                return ResponseEntity.badRequest().body(Constants.JOB_ID_CAN_NOT_BE_NULL);
            }

            CustomerJobDetails customerJobDetails = customerJobDetailsMap
                    .getOrDefault(payload.jobId(), null);

            if (customerJobDetails == null) {
                return ResponseEntity.badRequest().body(Constants.INVALID_JOB_ID);
            }

            customerJobDetails.setIsDeleted(true);
            customerJobDetails.setUpdatedByUserType(UserType.TENANT.name());
            customerJobDetails.setUpdatedBy(customerId);
            customerJobDetails.setUpdatedAt(today);

            deletableCustomerJobDetails.add(customerJobDetails);
        }

        if (!deletableCustomerJobDetails.isEmpty()) {
            customerJobDetailsRepository.saveAll(deletableCustomerJobDetails);
        }

        return ResponseEntity.ok(Utils.DELETED);
    }

    public List<CustomerJobDetails> getCustomerJobDetailsByCustomerId(String customerId) {
        return customerJobDetailsRepository
                .findAllByCustomerIdAndIsDeletedFalseOrderByJobIdDesc(customerId);
    }
}
