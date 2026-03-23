package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.CustomerAdditionalContacts;
import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.ennum.UserType;
import com.smartstay.tenant.payload.customer.CustomerAdditionalContactsIdPayload;
import com.smartstay.tenant.payload.customer.CustomerAdditionalContactsPayload;
import com.smartstay.tenant.repository.CustomerAdditionalContactsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerAdditionalContactsService {

    @Autowired
    private Authentication authentication;
    @Autowired
    private CustomerAdditionalContactsRepository customerAdditionalContactsRepository;
    @Autowired
    private CustomerDuplicateService customerService;

    public List<CustomerAdditionalContacts> getAdditionalContactsByCustomerId(String customerId) {
        return customerAdditionalContactsRepository
                .findAllByCustomerIdAndIsDeletedFalseOrderByCreatedAtDesc(customerId);
    }

    public List<CustomerAdditionalContacts> getAdditionalContactsByCustomerIdAndContactIds(String customerId,
                                                                                           Set<Long> contactIds) {
        return customerAdditionalContactsRepository
                .findAllByCustomerIdAndContactIdInAndIsDeletedFalseOrderByCreatedAtDesc(customerId, contactIds);
    }

    public ResponseEntity<?> addAdditionalContacts(List<CustomerAdditionalContactsPayload> payloads) {

        String customerId = authentication.getName();
        Customers customer = customerService.getCustomerById(customerId);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.CUSTOMER_NOT_FOUND);
        }

        for (CustomerAdditionalContactsPayload payload : payloads) {
            if (payload.name() == null || payload.name().trim().isEmpty()) {
                return new ResponseEntity<>(Utils.NAME_IS_REQUIRED, HttpStatus.BAD_REQUEST);
            }
            if (payload.mobile() == null || payload.mobile().trim().isEmpty()) {
                return new ResponseEntity<>(Utils.MOBILE_IS_REQUIRED, HttpStatus.BAD_REQUEST);
            }
        }

        List<CustomerAdditionalContacts> additionalContacts = new ArrayList<>();

        for (CustomerAdditionalContactsPayload payload : payloads) {
            CustomerAdditionalContacts additionalContact = new CustomerAdditionalContacts();
            additionalContact.setName(payload.name());
            additionalContact.setRelationship(payload.relationship());
            additionalContact.setOccupation(payload.occupation());
            additionalContact.setMobile(payload.mobile());
            additionalContact.setFullAddress(payload.fullAddress());
            additionalContact.setCustomerId(customerId);
            additionalContact.setHostelId(customer.getHostelId());
            additionalContact.setCountryCode(payload.countryCode());
            additionalContact.setAddedByUserType(UserType.TENANT.name());
            additionalContact.setUpdatedByUserType(null);
            additionalContact.setDeleted(false);
            additionalContact.setCreatedBy(customerId);
            additionalContact.setUpdatedBy(null);
            additionalContact.setCreatedAt(new Date());
            additionalContact.setUpdatedAt(null);

            additionalContacts.add(additionalContact);
        }

        customerAdditionalContactsRepository.saveAll(additionalContacts);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<?> deleteAdditionalContacts(List<CustomerAdditionalContactsIdPayload> payloads) {

        String customerId = authentication.getName();
        Customers customer = customerService.getCustomerById(customerId);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.CUSTOMER_NOT_FOUND);
        }

        if (payloads == null || payloads.isEmpty()) {
            return new ResponseEntity<>(Utils.NO_CONTACT_ID_PROVIDED, HttpStatus.BAD_REQUEST);
        }

        for (CustomerAdditionalContactsIdPayload payload : payloads) {
            if (payload.contactId() == null) {
                return new ResponseEntity<>(Utils.CONTACT_ID_REQUIRED, HttpStatus.BAD_REQUEST);
            }
            if (payload.contactId() <= 0) {
                return new ResponseEntity<>(Utils.CONTACT_ID_CANT_BE_ZERO_OR_LESS, HttpStatus.BAD_REQUEST);
            }
        }

        Set<Long> contactIds = payloads.stream()
                .map(CustomerAdditionalContactsIdPayload::contactId)
                .collect(Collectors.toSet());

        List<CustomerAdditionalContacts> additionalContacts = customerAdditionalContactsRepository
                .findAllByContactIdInAndCustomerIdAndIsDeletedFalse(contactIds, customerId);

        additionalContacts.forEach(additionalContact -> {
            additionalContact.setDeleted(true);
            additionalContact.setUpdatedAt(new Date());
            additionalContact.setUpdatedBy(customerId);
            additionalContact.setUpdatedByUserType(UserType.TENANT.name());
        });

        customerAdditionalContactsRepository.saveAll(additionalContacts);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public void saveAll(List<CustomerAdditionalContacts> additionalContacts) {
        customerAdditionalContactsRepository.saveAll(additionalContacts);
    }
}
