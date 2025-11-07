package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.BillingRules;
import com.smartstay.tenant.dto.BillingDates;
import com.smartstay.tenant.dto.ComplaintDTO;
import com.smartstay.tenant.repository.HostelRepository;
import com.smartstay.tenant.response.customer.CustomerHostels;
import com.smartstay.tenant.response.hostel.HostelDetails;
import com.smartstay.tenant.response.hostel.InvoiceItems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class HostelService {


    @Autowired
    private HostelConfigService hostelConfigService;


    @Autowired
    private ComplaintService complaintService;


    @Autowired
    private InvoiceService invoiceService;


    @Autowired
    private HostelRepository hostelRepository;

    @Autowired
    private Authentication authentication;

    public ResponseEntity<?> getHostels() {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Utils.UNAUTHORIZED);
        }
        String customerId = authentication.getName();
        List<CustomerHostels> hostels = hostelRepository.findHostels(customerId);
        return new ResponseEntity<>(hostels, HttpStatus.OK);

    }

    public ResponseEntity<?> getHostelDetails(String hostelId) {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Utils.UNAUTHORIZED);
        }
        String customerId = authentication.getName();
        BillingDates previousBillingDates = getBillStartDate(hostelId, Utils.getFirstDayOfPreviousMonth());
        BillingDates currentBillingDates = getBillStartDate(hostelId, new Date());
        List<InvoiceItems> previousMonthInvoices = invoiceService.getInvoicesWithItems(customerId, previousBillingDates.currentBillStartDate(), previousBillingDates.currentBillEndDate());

        List<InvoiceItems> currentMonthInvoices = invoiceService.getInvoicesWithItems(customerId, currentBillingDates.currentBillStartDate(), currentBillingDates.currentBillEndDate());

        List<ComplaintDTO> complaints = complaintService.getComplaints(hostelId, customerId);
        HostelDetails hostelDetails = new HostelDetails(previousMonthInvoices, currentMonthInvoices, complaints);
        return new ResponseEntity<>(hostelDetails, HttpStatus.OK);

    }


    public BillingDates getCurrentBillStartAndEndDates(String hostelId) {
        BillingRules billingRules = hostelConfigService.getCurrentMonthTemplate(hostelId);
        int billStartDate = 1;
        if (billingRules != null) {
            billStartDate = billingRules.getBillingStartDate();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, billStartDate);

        Date findEndDate = Utils.findLastDate(billStartDate, calendar.getTime());

        return new BillingDates(calendar.getTime(), findEndDate);
    }

    public BillingDates getBillStartDate(String hostelId, Date date) {
        BillingRules billingRules = hostelConfigService.getCurrentMonthTemplate(hostelId);
        int billStartDate = 1;
        if (billingRules != null) {
            billStartDate = billingRules.getBillingStartDate();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, billStartDate);

        Date findEndDate = Utils.findLastDate(billStartDate, calendar.getTime());

        return new BillingDates(calendar.getTime(), findEndDate);
    }


}
