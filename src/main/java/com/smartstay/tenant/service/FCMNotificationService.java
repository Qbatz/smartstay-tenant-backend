package com.smartstay.tenant.service;


import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dao.InvoicesV1;
import com.smartstay.tenant.repository.InvoicesV1Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    private CustomerCredentialsService customerCredentialsService;


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
}
