package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.BillingRules;
import com.smartstay.tenant.dto.BedChangeRequestResponse;
import com.smartstay.tenant.dto.BillingDates;
import com.smartstay.tenant.dto.ComplaintDTO;
import com.smartstay.tenant.ennum.RequestType;
import com.smartstay.tenant.repository.BedsRepository;
import com.smartstay.tenant.repository.FloorRepository;
import com.smartstay.tenant.repository.HostelRepository;
import com.smartstay.tenant.repository.RoomRepository;
import com.smartstay.tenant.response.amenity.AmenityRequestResponse;
import com.smartstay.tenant.response.customer.CustomerHostels;
import com.smartstay.tenant.response.dashboard.InvoiceSummaryResponse;
import com.smartstay.tenant.response.hostel.HostelDetails;
import com.smartstay.tenant.response.hostel.InvoiceSummary;
import com.smartstay.tenant.response.hostel.RequestItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class HostelService {


    @Autowired
    private HostelConfigService hostelConfigService;


    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private CustomerService customerService;


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

    @Autowired
    private AmenityRequestService amenityRequestService;

    @Autowired
    private BedChangeRequestService bedChangeRequestService;

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

        InvoiceSummary previousSummary = previousMonthInvoices != null ? new InvoiceSummary(previousMonthInvoices.getRent(), previousMonthInvoices.getEb(), previousMonthInvoices.getPaidAmount(), previousMonthInvoices.getInvoiceNumber(), previousMonthInvoices.getInvoiceGeneratedDate(), previousMonthInvoices.getInvoiceDueDate(), previousMonthInvoices.getCurrentInvoiceStartDate(), previousMonthInvoices.getCurrentInvoiceEndDate(), isToday(previousMonthInvoices.getInvoiceGeneratedDate()), buildHint(previousMonthInvoices), buildMessage(previousMonthInvoices)) : null;

        InvoiceSummary currentSummary = currentMonthInvoices != null ? new InvoiceSummary(currentMonthInvoices.getRent(), currentMonthInvoices.getEb(), currentMonthInvoices.getPaidAmount(), currentMonthInvoices.getInvoiceNumber(), currentMonthInvoices.getInvoiceGeneratedDate(), currentMonthInvoices.getInvoiceDueDate(), currentMonthInvoices.getCurrentInvoiceStartDate(), currentMonthInvoices.getCurrentInvoiceEndDate(), isToday(currentMonthInvoices.getInvoiceGeneratedDate()), buildHint(currentMonthInvoices), buildMessage(currentMonthInvoices)) : null;

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

    private String buildHint(InvoiceSummaryResponse invoice) {

        boolean isEb = invoice.getEb() != null && invoice.getEb() > 0;

        LocalDate invoiceMonth = invoice.getInvoiceGeneratedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        String monthName = invoiceMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        if (isEb) {
            String previousMonthName = invoiceMonth.minusMonths(1).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

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

    private RequestItemResponse mapAmenity(AmenityRequestResponse a) {
        return new RequestItemResponse(a.getRequestId(), RequestType.AMENITY_REQUEST.name(), a.getRequestedDate(), a.getStatus(), "Amenity Request", a.getDescription(), a.getAmenityName());
    }

    private RequestItemResponse mapBed(BedChangeRequestResponse b) {

        String requestedItem = b.getBedName() + " | " + b.getRoomName() + " | " + b.getFloorName();

        return new RequestItemResponse(b.getRequestId(), RequestType.CHANGE_BED.name(), b.getStartsFrom(), b.getCurrentStatus(), "Bed Change Request", b.getReason(), requestedItem);
    }

    public ResponseEntity<?> getCustomerRequests(String hostelId) {

        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        List<AmenityRequestResponse> amenityRequests = amenityRequestService.getRequests(customerId, hostelId);
        List<BedChangeRequestResponse> bedRequests = bedChangeRequestService.getRequests(customerId, hostelId);

        List<RequestItemResponse> unifiedList = new ArrayList<>();

        amenityRequests.forEach(ar -> unifiedList.add(mapAmenity(ar)));
        bedRequests.forEach(br -> unifiedList.add(mapBed(br)));

        unifiedList.sort((a, b) -> b.getRequestedDate().compareTo(a.getRequestedDate()));

        return new ResponseEntity<>(unifiedList, HttpStatus.OK);
    }


    public ResponseEntity<?> getCustomerRequestById(String hostelId, Long requestId, String requestType) {

        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        String customerId = authentication.getName();

        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        if (requestType.equals(RequestType.AMENITY_REQUEST.name())) {
            AmenityRequestResponse amenityRequest = amenityRequestService.getRequestById(customerId, hostelId, requestId);
            if (amenityRequest == null) {
                return new ResponseEntity<>(Utils.NO_RECORDS_FOUND, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(mapAmenity(amenityRequest), HttpStatus.OK);
        } else if (requestType.equals(RequestType.CHANGE_BED.name())) {
            BedChangeRequestResponse bedRequest = bedChangeRequestService.getRequestsById(customerId, hostelId, requestId);
            if (bedRequest == null) {
                return new ResponseEntity<>(Utils.NO_RECORDS_FOUND, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(mapBed(bedRequest), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Utils.INVALID_REQUEST_TYPE, HttpStatus.BAD_REQUEST);
        }
    }


}
