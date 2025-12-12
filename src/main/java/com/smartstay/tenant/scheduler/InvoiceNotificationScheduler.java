package com.smartstay.tenant.scheduler;


import com.smartstay.tenant.service.FCMNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InvoiceNotificationScheduler {

    @Autowired
    private FCMNotificationService fcmNotificationService;

    @Scheduled(cron = "0 0 8 * * *")
//    @Scheduled(fixedRate = 10000)
    public void scheduleInvoiceNotifications() {
        fcmNotificationService.notifyCustomersForTodayInvoices();
    }
}
