package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.*;
import com.smartstay.tenant.dto.BillingDates;
import com.smartstay.tenant.dto.ComplaintDTO;
import com.smartstay.tenant.repository.BedsRepository;
import com.smartstay.tenant.repository.FloorRepository;
import com.smartstay.tenant.repository.HostelRepository;
import com.smartstay.tenant.repository.RoomRepository;
import com.smartstay.tenant.response.customer.CustomerHostels;
import com.smartstay.tenant.response.dashboard.InvoiceSummaryResponse;
import com.smartstay.tenant.response.hostel.HostelDetails;
import com.smartstay.tenant.response.hostel.InvoiceItems;
import com.smartstay.tenant.response.hostel.InvoiceSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    @Autowired
    private UserHostelService userHostelService;


    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private BedsRepository bedsRepository;

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
        BillingDates currentBillingDates = getCurrentBillStartAndEndDates(hostelId);
        InvoiceSummaryResponse previousMonthInvoices = invoiceService.getLatestInvoiceSummary(customerId, previousBillingDates.currentBillStartDate(), previousBillingDates.currentBillEndDate());
        InvoiceSummaryResponse currentMonthInvoices = invoiceService.getLatestInvoiceSummary(customerId, currentBillingDates.currentBillStartDate(), currentBillingDates.currentBillEndDate());

        InvoiceSummary previousSummary = previousMonthInvoices != null
                ? new InvoiceSummary(
                previousMonthInvoices.getRent(),
                previousMonthInvoices.getEb(),
                previousMonthInvoices.getPaidAmount(),
                previousMonthInvoices.getInvoiceNumber(),
                previousMonthInvoices.getInvoiceGeneratedDate(),
                previousMonthInvoices.getInvoiceDueDate(),
                previousMonthInvoices.getCurrentInvoiceStartDate(),
                previousMonthInvoices.getCurrentInvoiceEndDate(),
                isToday(previousMonthInvoices.getInvoiceGeneratedDate()),
                buildHint(previousMonthInvoices),
                buildMessage(previousMonthInvoices)
        )
                : null;

        InvoiceSummary currentSummary = currentMonthInvoices != null
                ? new InvoiceSummary(
                currentMonthInvoices.getRent(),
                currentMonthInvoices.getEb(),
                currentMonthInvoices.getPaidAmount(),
                currentMonthInvoices.getInvoiceNumber(),
                currentMonthInvoices.getInvoiceGeneratedDate(),
                currentMonthInvoices.getInvoiceDueDate(),
                currentMonthInvoices.getCurrentInvoiceStartDate(),
                currentMonthInvoices.getCurrentInvoiceEndDate(),
                isToday(currentMonthInvoices.getInvoiceGeneratedDate()),
                buildHint(currentMonthInvoices),
                buildMessage(currentMonthInvoices)
        )
                : null;



        List<ComplaintDTO> complaints = complaintService.getComplaints(hostelId, customerId);
        HostelDetails hostelDetails = new HostelDetails(previousSummary, currentSummary, complaints);
        return new ResponseEntity<>(hostelDetails, HttpStatus.OK);

    }

    public boolean isToday(Date date) {
        LocalDate given = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();
        return given.equals(today);
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

    Beds findByBedIdAndParentIdAndHostelId(Integer bedId, String parentId, String hostelId) {
        return bedsRepository.findByBedIdAndParentIdAndHostelId(bedId, parentId, hostelId);
    }

    Floors findByFloorIdAndHostelId(Integer floorId, String hostelId) {
        return floorRepository.findByFloorIdAndHostelId(floorId, hostelId);
    }

    Rooms findByRoomIdAndParentIdAndHostelId(Integer roomId, String parentId, String hostelId) {
        return roomRepository.findByRoomIdAndParentIdAndHostelId(roomId, parentId, hostelId);
    }

    Rooms findByRoomIdAndParentIdAndHostelIdAndFloorId(int roomId, String parentId, String hostelId, int floorId) {
        return roomRepository.findByRoomIdAndParentIdAndHostelIdAndFloorId(roomId, parentId, hostelId, floorId);
    }

    Beds findByBedIdAndRoomIdAndParentId(Integer bedId,int roomId, String parentId) {
        return bedsRepository.findByBedIdAndRoomIdAndParentId(bedId, roomId, parentId);
    }

    private String buildHint(InvoiceSummaryResponse invoice) {

        boolean isEb = invoice.getEb() != null && invoice.getEb() > 0;

        LocalDate invoiceMonth = invoice.getInvoiceGeneratedDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        String monthName = invoiceMonth.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        if (isEb) {
            String previousMonthName = invoiceMonth.minusMonths(1)
                    .getMonth()
                    .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            return previousMonthName + " EB bill generated";
        } else {
            return monthName + " rent bill generated";
        }
    }

    private String buildMessage(InvoiceSummaryResponse invoice) {
        if (invoice == null || invoice.getInvoiceGeneratedDate() == null) {
            return "";
        }

        String monthName = getMonthName(invoice.getInvoiceGeneratedDate());
        return monthName + " month bill generated";
    }

    private String getMonthName(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM", Locale.ENGLISH);
        return sdf.format(date);
    }






}
