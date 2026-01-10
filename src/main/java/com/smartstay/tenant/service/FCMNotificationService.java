package com.smartstay.tenant.service;


import com.google.firebase.messaging.*;
import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.dao.InvoicesV1;
import com.smartstay.tenant.dao.Users;
import com.smartstay.tenant.dao.UsersConfig;
import com.smartstay.tenant.repository.InvoicesV1Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class FCMNotificationService {

    @Autowired
    private FirebaseMessaging tenantMessaging;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private InvoicesV1Repository invoicesV1Repository;
    @Autowired
    private Authentication authentication;
    @Autowired
    private CustomerCredentialsService customerCredentialsService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private UserService userService;

    public ResponseEntity<?> sendTestMessage() {
        Message message = Message.builder().setToken("eutSAEryT3uMKc98cnVOfY:APA91bEhI2eWe1Iv5nbyqx4SulyV8nlVc86uNKYPBDCaAZhR8o5FnKfWggbcrftdmQ2Nqc8uHZXwhhUDOdGtMMXm9ZkQURTpOhKFsBq65OTA67sNgZUiUtY").putData("test", "testing.....").putData("title", "test titile").putData("body", "This is sample body data").build();

        try {
            return new ResponseEntity<>(tenantMessaging.send(message), HttpStatus.OK);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }


    public void notifyCustomersForTodayInvoices() {
        List<InvoicesV1> invoices = invoicesV1Repository.findInvoicesGeneratedTodayForActiveCustomers();

        for (InvoicesV1 invoice : invoices) {
            String fCmToken = customerCredentialsService.getFCmToken(invoice.getCustomerId());
            if (fCmToken != null && !fCmToken.isEmpty()) {
                notificationService.createNotificationForInvoiceGeneration(invoice.getInvoiceId(), "Invoice Generated", "Hi " + invoice.getCustomerMobile() + ", your " + Utils.capitalize(invoice.getInvoiceType()) + " invoice for this month has been generated.", invoice.getCustomerId(), invoice.getHostelId());
                HashMap<String, String> payloads = new HashMap<>();
                payloads.put("title", "Invoice Generated");
                payloads.put("type", "INVOICE_NOTIFICATION");
                payloads.put("description", "Hi " + invoice.getCustomerMobile() + ", your " + Utils.capitalize(invoice.getInvoiceType()) + " invoice for this month has been generated.");
                Message message = Message.builder().setToken(fCmToken).putAllData(payloads).build();
                try {
                    tenantMessaging.send(message);
                } catch (FirebaseMessagingException e) {
                }
            }
        }
    }

    public void sendCreateComplaintNotification(String hostelId, String complaintType, String description) {
        if (!authentication.isAuthenticated()) {
            return;
        }

        List<Users> adminUsers = userService.findMasters(hostelId);

        Customers customers = customerService.getCustomerById(authentication.getName());
        if (customers != null) {
            StringBuilder fullName = new StringBuilder();
            if (customers.getFirstName() != null) {
                fullName.append(customers.getFirstName());
            }
            if (customers.getLastName() != null && !customers.getLastName().trim().equalsIgnoreCase("")) {
                fullName.append(" ");
                fullName.append(customers.getLastName());
            }

            if (adminUsers != null) {
                List<String> fcmTokens = new ArrayList<>();
                List<UsersConfig> userConfigs = adminUsers
                        .stream()
                        .map(Users::getConfig)
                        .toList();
                if (userConfigs != null) {
                    userConfigs.forEach(item -> {
                       if (item != null) {
                           if (item.getFcmToken() != null) {
                               fcmTokens.add(item.getFcmToken());
                           }
                           if (item.getFcmWebToken() != null) {
                               fcmTokens.add(item.getFcmWebToken());
                           }
                       }
                    });
                }

                if (!fcmTokens.isEmpty()) {
                    HashMap<String, String> payloads = new HashMap<>();
                    payloads.put("title", "New complaints for " + complaintType);
                    payloads.put("type", "COMPLAINTS_RAISED");
                    payloads.put("description", "Hi," + fullName.toString() + " has raised a complaints for" + complaintType);

                    MulticastMessage multicastMessage = MulticastMessage.builder()
                            .addAllTokens(fcmTokens)
                            .putAllData(payloads)
                            .build();

                    try {
                        BatchResponse response = FirebaseMessaging.getInstance()
                                .sendEachForMulticast(multicastMessage);
                    } catch (FirebaseMessagingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }
    }

    public void sendNotificationBedChangeRequest(String hostelId) {
        if (!authentication.isAuthenticated()) {
            return;
        }

        List<Users> adminUsers = userService.findMasters(hostelId);

        Customers customers = customerService.getCustomerById(authentication.getName());
        if (customers != null) {
            StringBuilder fullName = new StringBuilder();
            if (customers.getFirstName() != null) {
                fullName.append(customers.getFirstName());
            }
            if (customers.getLastName() != null && !customers.getLastName().trim().equalsIgnoreCase("")) {
                fullName.append(" ");
                fullName.append(customers.getLastName());
            }

            if (adminUsers != null) {
                List<String> fcmTokens = new ArrayList<>();
                List<UsersConfig> userConfigs = adminUsers
                        .stream()
                        .map(Users::getConfig)
                        .toList();
                if (userConfigs != null) {
                    userConfigs.forEach(item -> {
                        if (item != null) {
                            if (item.getFcmToken() != null) {
                                fcmTokens.add(item.getFcmToken());
                            }
                            if (item.getFcmWebToken() != null) {
                                fcmTokens.add(item.getFcmWebToken());
                            }
                        }
                    });
                }

                if (!fcmTokens.isEmpty()) {
                    HashMap<String, String> payloads = new HashMap<>();
                    payloads.put("title", "Bed Change Request");
                    payloads.put("type", "BED_CHANGE_REQUEST");
                    payloads.put("description", fullName+" has raised a bed change request. Review and respond now.");

                    MulticastMessage multicastMessage = MulticastMessage.builder()
                            .addAllTokens(fcmTokens)
                            .putAllData(payloads)
                            .build();

                    try {
                        BatchResponse response = FirebaseMessaging.getInstance()
                                .sendEachForMulticast(multicastMessage);
                    } catch (FirebaseMessagingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }
    }
}
