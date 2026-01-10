package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.InvoiceUtils;
import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.*;
import com.smartstay.tenant.dto.BedDetails;
import com.smartstay.tenant.dto.BillingDates;
import com.smartstay.tenant.dto.bills.PaymentHistoryProjection;
import com.smartstay.tenant.dto.invoice.Deductions;
import com.smartstay.tenant.dto.invoice.*;
import com.smartstay.tenant.ennum.BankAccountType;
import com.smartstay.tenant.ennum.BillConfigTypes;
import com.smartstay.tenant.ennum.InvoiceType;
import com.smartstay.tenant.mapper.invoice.InvoiceItemMapper;
import com.smartstay.tenant.mapper.invoice.InvoiceSummaryMapper;
import com.smartstay.tenant.repository.HostelRepository;
import com.smartstay.tenant.repository.InvoicesV1Repository;
import com.smartstay.tenant.response.dashboard.InvoiceSummaryResponse;
import com.smartstay.tenant.response.hostel.InvoiceItems;
import com.smartstay.tenant.response.invoices.*;
import com.smartstay.tenant.response.receipt.ReceiptConfigInfo;
import com.smartstay.tenant.response.receipt.ReceiptDetails;
import com.smartstay.tenant.response.receipt.ReceiptInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class InvoiceService {


    @Autowired
    private Authentication authentication;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private InvoicesV1Repository invoicesV1Repository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private HostelRepository hostelRepository;

    @Autowired
    private HostelConfigService hostelConfigService;

    @Autowired
    private TemplatesService templatesService;

    @Autowired
    private BankingService bankingService;

    @Autowired
    private CustomerBedHistoryService customerBedHistoryService;

    @Autowired
    private BedsService bedService;

    @Autowired
    private UserService userService;
    private HostelService hostelService;

    public static long calculateBillableDays(Date invoiceStartDate, Date joiningDate, Date invoiceEndDate) {
        if (invoiceStartDate == null || joiningDate == null || invoiceEndDate == null) {
            throw new IllegalArgumentException("Dates must not be null");
        }

        LocalDate startDate = toLocalDate(invoiceStartDate);
        LocalDate joinDate = toLocalDate(joiningDate);
        LocalDate endDate = toLocalDate(invoiceEndDate);

        // Decide effective start date
        LocalDate effectiveStartDate = joinDate.isAfter(startDate) ? joinDate : startDate;

        return ChronoUnit.DAYS.between(effectiveStartDate, endDate) + 1;
    }

    private static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Autowired
    public void setHostelService(@Lazy HostelService hostelService) {
        this.hostelService = hostelService;
    }

    public List<InvoiceItems> getInvoicesWithItems(String customerId, Date startDate, Date endDate) {
        return invoicesV1Repository.getInvoiceItemDetails(customerId, startDate, endDate, List.of(InvoiceType.EB.name(), InvoiceType.RENT.name()));
    }

    public InvoiceSummaryResponse getLatestInvoiceSummary(String hostelId, String customerId, Date startDate, Date endDate) {
        InvoiceSummaryProjection projection = invoicesV1Repository.getInvoiceSummary(hostelId, customerId, startDate, endDate);

        System.out.println("Invoice Summary Projection: " + projection);
        System.out.println("DueDate---> " + (projection != null ? projection.getInvoiceDueDate() : "null"));
        if (projection == null) {
            return null;
        }
        return new InvoiceSummaryMapper().apply(projection);
    }

    public ResponseEntity<?> getInvoiceList(String hostelId) {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Utils.UNAUTHORIZED);
        }
        String customerId = authentication.getName();
        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        HostelV1 hostelV1 = hostelRepository.findByHostelIdAndIsActiveTrueAndIsDeletedFalse(hostelId);

        List<InvoiceItemProjection> invoiceItems = invoicesV1Repository.getAllInvoiceItems(hostelId, customerId);
        InvoiceItemMapper invoiceItemMapper = new InvoiceItemMapper();

        List<InvoiceItemResponseDTO> invoiceItemDTOs = invoiceItems.stream().map(invoiceItemMapper).toList();
        if (invoiceItemDTOs.isEmpty()) {
            return new ResponseEntity<>(Utils.INVOICE_ITEMS_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        InvoiceListDto invoiceListDto = new InvoiceListDto();
        invoiceListDto.setInvoices(invoiceItemDTOs);
        invoiceListDto.setHostelName(hostelV1.getHostelName());
        invoiceListDto.setHostelUrl(hostelV1.getMainImage());
        invoiceListDto.setInitials(Utils.getInitials(hostelV1.getHostelName()));
        return new ResponseEntity<>(invoiceListDto, HttpStatus.OK);

    }

    public ResponseEntity<?> getInvoicesById(String hostelId, String invoiceId) {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Utils.UNAUTHORIZED);
        }
        String customerId = authentication.getName();
        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        InvoicesV1 invoice = invoicesV1Repository.getInvoiceByIdAndCustomerId(invoiceId, customerId);

        if (invoice == null) {
            return new ResponseEntity<>(Utils.INVOICE_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        if (invoice.getInvoiceType().equalsIgnoreCase(InvoiceType.SETTLEMENT.name())) {
            return getFinalSettlementInvoiceDetails(customerId, invoice);
        }
        InvoiceDetailsDTO invoiceItem = getInvoiceDetails(invoiceId, invoice);
        if (invoiceItem == null) {
            return new ResponseEntity<>(Utils.INVOICE_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(invoiceItem, HttpStatus.OK);

    }

    private ResponseEntity<?> getFinalSettlementInvoiceDetails(String customerId, InvoicesV1 invoice) {
        FinalSettlementDetails finalSettlementDetails = new FinalSettlementDetails();

        List<InvoiceItemDTO> invoiceItems = invoicesV1Repository.getInvoiceItems(invoice.getInvoiceId());

        Double totalPaid = transactionService.getTotalPaidAmountByInvoiceId(invoice.getInvoiceId());
        if (totalPaid == null) totalPaid = 0.0;

        double dueAmount = invoice.getTotalAmount() - totalPaid;

        String status = dueAmount == 0 ? "Paid" : totalPaid == 0 ? "Pending" : "Partially Paid";

        List<ReceiptDTO> receipts = transactionService.getReceiptsByInvoiceId(invoice.getInvoiceId());

        TransactionV1 latestTransaction = transactionService.getLatestTransactionByInvoiceId(invoice.getInvoiceId());

        Date lastPaidDate = null;
        String lastPaymentMode = null;
        String referenceId = null;

        if (latestTransaction != null) {
            lastPaidDate = latestTransaction.getPaymentDate();
            referenceId = latestTransaction.getTransactionReferenceId();

            if (latestTransaction.getBankId() != null) {
                BankingV1 bank = transactionService.getBankDetailsById(latestTransaction.getBankId());
                if (bank != null && bank.getBankName() != null) {
                    lastPaymentMode = Utils.capitalize(bank.getBankName());
                }
            }
        }

        boolean showMessage = false;
        Date today = new Date();

        if ("Pending".equalsIgnoreCase(status) && invoice.getInvoiceDueDate().before(today)) {
            showMessage = true;
        }

        Customers customers = customerService.getCustomerById(customerId);

        AdvanceInfo advanceInfo = null;
        InvoicesV1 advanceInvoice = invoicesV1Repository.findAdvanceInvoice(invoice.getCustomerId(), invoice.getHostelId());
        InvoicesV1 bookingInvoice = invoicesV1Repository.findBookingInvoice(invoice.getCustomerId(), invoice.getHostelId());

        double advanceAmount = 0.0;
        double bookingAmount = 0.0;
        double advancePaid = 0.0;
        double totalAdvancePaid = 0.0;
        String advanceInvoiceNumber = null;
        List<Deductions> listDeductions = new ArrayList<>();

        if (advanceInvoice != null) {
            advanceInvoiceNumber = advanceInvoice.getInvoiceNumber();
            advanceAmount = advanceInvoice.getTotalAmount();
            if (advanceInvoice.getPaidAmount() != null) {
                advancePaid = advanceInvoice.getPaidAmount();
            }

        }
        if (bookingInvoice != null) {
            bookingAmount = bookingInvoice.getTotalAmount();
        }

        if (customers != null) {
            Advance advance = customers.getAdvance();
            if (advance != null) {
                listDeductions = advance.getDeductions().stream().map(i -> new Deductions(i.getType(), i.getAmount())).toList();
            }
        }

        totalAdvancePaid = advancePaid + bookingAmount;
        advanceInfo = new AdvanceInfo(advanceAmount, advancePaid, advanceInvoiceNumber, bookingAmount, totalAdvancePaid, listDeductions);

        BillingDates billingDates = hostelService.getBillStartDate(invoice.getHostelId(), invoice.getInvoiceStartDate());
        List<InvoicesV1> currentMonthRentalInvoices = null;
        if (billingDates != null) {
            currentMonthRentalInvoices = invoicesV1Repository.findCurrentMonthInvoices(customerId, billingDates.currentBillStartDate(), billingDates.currentBillEndDate());
        }

        CurrentMonthInfo currentMonthInfo = null;

        Date invoiceStartDate = invoice.getInvoiceStartDate();

        BillingDates billingDate = hostelService.getBillStartAndEndDateBasedOnDate(invoice.getHostelId(), invoiceStartDate);

        System.out.println("Billing Start Date: " + billingDate.currentBillStartDate() + ", Billing End Date: " + billingDate.currentBillEndDate());
        System.out.println("customerId: " + customerId);
        List<CustomersBedHistory> customersBedHistories = customerBedHistoryService.getCustomerBedByDates(customerId, billingDate.currentBillStartDate(), invoice.getInvoiceEndDate());

        System.out.println("Customers Bed Histories: " + customersBedHistories.size());

        Date billingStart = billingDate.currentBillStartDate(); // Dec 01
        Date billingEnd = billingDate.currentBillEndDate();     // Dec 31

        long noOfDaysStayed = 0;

        for (CustomersBedHistory bedHistory : customersBedHistories) {

            Date bedStart = bedHistory.getStartDate();
            Date bedEnd = bedHistory.getEndDate();

            // If end date is null, treat as invoice generated date
            if (bedEnd == null) {
                bedEnd = invoice.getInvoiceGeneratedDate();
            }

            // Clip to billing window
            Date effectiveStart = bedStart.before(billingStart) ? billingStart : bedStart;
            Date effectiveEnd = bedEnd.after(billingEnd) ? billingEnd : bedEnd;

            // If no overlap, skip
            if (effectiveStart.after(effectiveEnd)) {
                continue;
            }

            long noOfDays = Utils.findNumberOfDays(effectiveStart, effectiveEnd);

            System.out.println("Effective Start: " + effectiveStart + ", Effective End: " + effectiveEnd + ", Days: " + noOfDays);

            noOfDaysStayed += noOfDays;
        }


        List<InvoicesV1> currentMonthInvoices = invoicesV1Repository.findAllCurrentMonthInvoices(customers.getCustomerId(), customers.getHostelId(), billingDate.currentBillStartDate());

        InvoicesV1 findLatestInvoice = invoicesV1Repository.findCurrentRunningInvoice(customers.getCustomerId(), billingDate.currentBillStartDate());

        List<InvoicesV1> currentMonthInvoicesBeforeBedChange;

        if (findLatestInvoice != null) {
            currentMonthInvoicesBeforeBedChange = currentMonthInvoices.stream().filter(i -> !i.getInvoiceId().equalsIgnoreCase(findLatestInvoice.getInvoiceId())).toList();
        } else {
            currentMonthInvoicesBeforeBedChange = currentMonthInvoices;
        }

        double payableRent = 0.0;
        for (InvoicesV1 inv : currentMonthInvoicesBeforeBedChange) {
            payableRent += inv.getTotalAmount();
        }

        CustomersBedHistory latestBedHistory = customerBedHistoryService.getLatestRentAmount(customerId);

        double latestRentAmount = latestBedHistory != null ? latestBedHistory.getRentAmount() : 0.0;

        long totalBillingDays = Utils.findNumberOfDays(billingDate.currentBillStartDate(), billingDate.currentBillEndDate());

        double perDayRent = totalBillingDays > 0 ? latestRentAmount / totalBillingDays : 0.0;

        if (findLatestInvoice != null) {

            Date startDate = findLatestInvoice.getInvoiceStartDate();
            Date endDate = findLatestInvoice.getInvoiceEndDate();

            if (startDate.before(billingDate.currentBillStartDate())) {
                startDate = billingDate.currentBillStartDate();
            }

            if (endDate.after(billingDate.currentBillEndDate())) {
                endDate = billingDate.currentBillEndDate();
            }

            if (!startDate.after(endDate)) {
                long noOfDay1 = Utils.findNumberOfDays(startDate, endDate);
                payableRent += perDayRent * noOfDay1;
            }
        }

        double lastRentPaid = invoicesV1Repository.getTotalPaidAmountForCurrentMonth(customerId, invoice.getHostelId(), billingDate.currentBillStartDate());

        List<BedHistory> customersBedHistoriesForLastMonth = customerBedHistoryService.getBedHistory(invoice.getInvoiceGeneratedDate(),customerId, invoice.getHostelId(), billingDate.currentBillStartDate(), billingDate.currentBillEndDate());

        currentMonthInfo = new CurrentMonthInfo(noOfDaysStayed, payableRent, lastRentPaid, customersBedHistoriesForLastMonth);


        finalSettlementDetails = new FinalSettlementDetails(invoice.getInvoiceId(), invoice.getInvoiceNumber(), Utils.capitalize(invoice.getInvoiceType()), invoice.getInvoiceGeneratedDate(), invoice.getInvoiceDueDate(), invoice.getInvoiceStartDate(), invoice.getInvoiceEndDate(), invoice.getTotalAmount(), totalPaid, dueAmount, status, invoice.getGst(), invoice.getCgst(), invoice.getSgst(), invoice.getGstPercentile(), invoiceItems, receipts, advanceInfo, currentMonthInfo, lastPaidDate, lastPaymentMode, referenceId, showMessage);

        return new ResponseEntity<>(finalSettlementDetails, HttpStatus.OK);
    }

    public InvoiceDetailsDTO getInvoiceDetails(String invoiceId, InvoicesV1 invoice) {

        List<InvoiceItemDTO> invoiceItems = invoicesV1Repository.getInvoiceItems(invoiceId);

        Double totalPaid = transactionService.getTotalPaidAmountByInvoiceId(invoiceId);
        if (totalPaid == null) totalPaid = 0.0;

        double dueAmount = invoice.getTotalAmount() - totalPaid;


        String status = "";
        if (invoice != null) {
            status = InvoiceUtils.getInvoicePaymentStatusByStatus(invoice.getPaymentStatus());
        }

        List<ReceiptDTO> receipts = transactionService.getReceiptsByInvoiceId(invoiceId);

        TransactionV1 latestTransaction = transactionService.getLatestTransactionByInvoiceId(invoiceId);

        Date lastPaidDate = null;
        String lastPaymentMode = null;
        String referenceId = null;

        if (latestTransaction != null) {
            lastPaidDate = latestTransaction.getPaymentDate();
            referenceId = latestTransaction.getTransactionReferenceId();

            if (latestTransaction.getBankId() != null) {
                BankingV1 bankingV1 = transactionService.getBankDetailsById(latestTransaction.getBankId());

                if (bankingV1.getAccountType().equalsIgnoreCase(BankAccountType.CASH.name())) {
                    lastPaymentMode = "Cash";
                }
                else if (bankingV1.getAccountType().equalsIgnoreCase(BankAccountType.CARD.name())) {
                    lastPaymentMode = "Card";
                }
                else if (bankingV1.getAccountType().equalsIgnoreCase(BankAccountType.UPI.name())) {
                    lastPaymentMode = "Upi";
                }
                else if (bankingV1.getAccountType().equalsIgnoreCase(BankAccountType.BANK.name())) {
                    lastPaymentMode = "Bank";
                }
            }
        }

        boolean showMessage = false;
        Date today = new Date();

        if ("Pending".equalsIgnoreCase(status) && invoice.getInvoiceDueDate().before(today)) {
            showMessage = true;
        }

        System.out.println("showMessage: " + Utils.dateToString(invoice.getInvoiceDueDate()));

        return new InvoiceDetailsDTO(invoice.getInvoiceId(), invoice.getInvoiceNumber(), Utils.capitalize(invoice.getInvoiceType()), Utils.dateToString(invoice.getInvoiceGeneratedDate()), Utils.dateToString(invoice.getInvoiceDueDate()), Utils.dateToString(invoice.getInvoiceStartDate()), Utils.dateToString(invoice.getInvoiceEndDate()), invoice.getTotalAmount(), totalPaid, dueAmount, status, invoice.getGst(), invoice.getCgst(), invoice.getSgst(), invoice.getGstPercentile(), invoiceItems, receipts, Utils.dateToString(lastPaidDate), lastPaymentMode, referenceId, showMessage);
    }


    public ResponseEntity<?> getReceiptDetailsByTransactionId(String hostelId, String transactionId) {
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        String customerId = authentication.getName();
        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        HostelV1 hostelV1 = hostelRepository.findByHostelId(hostelId);
        if (hostelV1 == null) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        TransactionV1 transactionV1 = transactionService.getTransactionById(transactionId);
        if (transactionV1 == null) {
            return new ResponseEntity<>(Utils.INVALID_TRANSACTION_ID, HttpStatus.BAD_REQUEST);
        }
        InvoicesV1 invoicesV1 = invoicesV1Repository.findById(transactionV1.getInvoiceId()).orElse(null);

        String hostelPhone = null;
        String hostelEmail = null;
        String invoiceType = "Rent";
        StringBuilder hostelFullAddress = new StringBuilder();
        String receiptSignatureUrl = null;
        String hostelLogo = null;

        if (invoicesV1.getInvoiceType().equalsIgnoreCase(InvoiceType.ADVANCE.name())) {
            invoiceType = "Advance";
        } else if (invoicesV1.getInvoiceType().equalsIgnoreCase(InvoiceType.BOOKING.name())) {
            invoiceType = "Booking";
        } else if (invoicesV1.getInvoiceType().equalsIgnoreCase(InvoiceType.SETTLEMENT.name())) {
            invoiceType = "Settlement";
        }

        if (hostelV1.getHouseNo() != null && !hostelV1.getHouseNo().equalsIgnoreCase("")) {
            hostelFullAddress.append(hostelV1.getHouseNo());
            hostelFullAddress.append(", ");
        }
        if (hostelV1.getStreet() != null && !hostelV1.getStreet().equalsIgnoreCase("")) {
            hostelFullAddress.append(hostelV1.getStreet());
            hostelFullAddress.append(", ");
        }
        if (hostelV1.getCity() != null && !hostelV1.getCity().equalsIgnoreCase("")) {
            hostelFullAddress.append(hostelV1.getCity());
            hostelFullAddress.append(", ");
        }
        if (hostelV1.getState() != null && !hostelV1.getState().equalsIgnoreCase("")) {
            hostelFullAddress.append(hostelV1.getState());
            hostelFullAddress.append("-");
        }
        if (hostelV1.getPincode() != 0) {
            hostelFullAddress.append(hostelV1.getPincode());
        }

        String invoiceMonth = null;
        if (invoicesV1.getInvoiceStartDate() != null) {
            BillingDates billingDates = hostelConfigService.getBillingRuleOnDate(invoicesV1.getHostelId(), invoicesV1.getInvoiceStartDate());
            if (billingDates != null) {
                if (billingDates.currentBillStartDate() != null) {
                    invoiceMonth = Utils.dateToMonth(billingDates.currentBillStartDate());
                }
            }
        }


        BankingV1 bankingV1 = bankingService.getBankDetails(transactionV1.getBankId());
        String bankName = null;
        if (bankingV1.getAccountType().equalsIgnoreCase(BankAccountType.CASH.name())) {
            bankName = "Cash";
        } else {
            bankName = bankingV1.getBankName();
        }
        String account = bankingV1.getAccountHolderName() + "-" + bankName;
        AccountDetails accountDetails = new AccountDetails(bankingV1.getAccountNumber(), bankingV1.getIfscCode(), account, bankingV1.getUpiId(), null);
        ReceiptConfigInfo receiptConfigInfo = null;
        BillTemplates hostelTemplates = templatesService.getTemplateByHostelId(hostelId);
        if (hostelTemplates != null) {
            if (!hostelTemplates.isMobileCustomized()) {
                hostelPhone = hostelTemplates.getMobile();
            } else {
                if (invoicesV1.getInvoiceType().equalsIgnoreCase(InvoiceType.ADVANCE.name()) || invoicesV1.getInvoiceType().equalsIgnoreCase(InvoiceType.BOOKING.name())) {
                    hostelPhone = hostelTemplates.getTemplateTypes().stream().filter(item -> item.getInvoiceType().equalsIgnoreCase(BillConfigTypes.ADVANCE.name())).map(BillTemplateType::getReceiptPhoneNumber).toList().getFirst();
                } else {
                    hostelPhone = hostelTemplates.getTemplateTypes().stream().filter(item -> item.getInvoiceType().equalsIgnoreCase(BillConfigTypes.RENTAL.name())).map(BillTemplateType::getReceiptPhoneNumber).toList().getFirst();
                }


            }

            if (!hostelTemplates.isEmailCustomized()) {
                hostelEmail = hostelTemplates.getEmailId();
            } else {
                if (invoicesV1.getInvoiceType().equalsIgnoreCase(InvoiceType.ADVANCE.name()) || invoicesV1.getInvoiceType().equalsIgnoreCase(InvoiceType.BOOKING.name())) {
                    hostelEmail = hostelTemplates.getTemplateTypes().stream().filter(item -> item.getInvoiceType().equalsIgnoreCase(BillConfigTypes.ADVANCE.name())).map(BillTemplateType::getReceiptMailId).toList().getFirst();
                } else {
                    hostelEmail = hostelTemplates.getTemplateTypes().stream().filter(item -> item.getInvoiceType().equalsIgnoreCase(BillConfigTypes.RENTAL.name())).map(BillTemplateType::getReceiptMailId).toList().getFirst();
                }

            }

            BillTemplateType templateType = null;
            if (invoicesV1.getInvoiceType().equalsIgnoreCase(InvoiceType.ADVANCE.name()) || invoicesV1.getInvoiceType().equalsIgnoreCase(InvoiceType.BOOKING.name())) {
                templateType = hostelTemplates.getTemplateTypes().stream().filter(item -> item.getInvoiceType().equalsIgnoreCase(BillConfigTypes.ADVANCE.name())).toList().getFirst();
            } else {
                templateType = hostelTemplates.getTemplateTypes().stream().filter(item -> item.getInvoiceType().equalsIgnoreCase(BillConfigTypes.RENTAL.name())).toList().getFirst();
            }


            if (!hostelTemplates.isSignatureCustomized()) {
                receiptSignatureUrl = hostelTemplates.getDigitalSignature();
            } else {
                receiptSignatureUrl = templateType.getReceiptSignatureUrl();
            }
            if (!hostelTemplates.isLogoCustomized()) {
                hostelLogo = hostelTemplates.getHostelLogo();
            } else {
                hostelLogo = templateType.getReceiptLogoUrl();
            }
            receiptConfigInfo = new ReceiptConfigInfo(templateType.getReceiptTermsAndCondition(), receiptSignatureUrl, hostelLogo, hostelFullAddress.toString(), templateType.getReceiptTemplateColor(), templateType.getReceiptNotes(), invoiceType);
        }

        Customers customers = customerService.getCustomerInformation(invoicesV1.getCustomerId());
        CustomerInfo customerInfo = null;
        if (customers != null) {
            StringBuilder fullName = new StringBuilder();
            StringBuilder fullAddress = new StringBuilder();
            if (customers.getFirstName() != null) {
                fullName.append(customers.getFirstName());
            }
            if (customers.getLastName() != null && !customers.getLastName().trim().equalsIgnoreCase("")) {
                fullName.append(", ");
                fullName.append(customers.getLastName());
            }
            if (customers.getHouseNo() != null && !customers.getHouseNo().trim().equalsIgnoreCase("")) {
                fullAddress.append(customers.getHouseNo());
                fullAddress.append(", ");
            }
            if (customers.getStreet() != null && !customers.getStreet().trim().equalsIgnoreCase("")) {
                fullAddress.append(customers.getStreet());
                fullAddress.append(", ");
            }
            if (customers.getCity() != null && !customers.getCity().trim().equalsIgnoreCase("")) {
                fullAddress.append(customers.getCity());
                fullAddress.append(", ");
            }
            if (customers.getState() != null && !customers.getState().trim().equalsIgnoreCase("")) {
                fullAddress.append(customers.getState());
                fullAddress.append("-");
            }

            if (customers.getPincode() != 0) {
                fullAddress.append(customers.getPincode());
            }

            customerInfo = new CustomerInfo(customers.getFirstName(), customers.getLastName(), fullName.toString(), customers.getCustomerId(), customers.getMobile(), "91", fullAddress.toString(), customers.getHouseNo(), customers.getStreet(), customers.getCity(), customers.getState(), customers.getPincode(), Utils.dateToString(customers.getJoiningDate()));
        }

        StayInfo stayInfo = new StayInfo(null, null, null, null,null);
        CustomersBedHistory bedHistory = null;
        if (!invoicesV1.getInvoiceType().equalsIgnoreCase(InvoiceType.BOOKING.name())) {
            assert customers != null;
            bedHistory = customerBedHistoryService.getCustomerBedByStartDate(customers.getCustomerId(), invoicesV1.getInvoiceStartDate(), invoicesV1.getInvoiceEndDate());
            if (bedHistory != null) {
                BedDetails bedDetails = bedService.getBedDetails(bedHistory.getBedId());
                if (bedDetails != null) {
                    stayInfo = new StayInfo(bedDetails.getBedName(), bedDetails.getFloorName(), bedDetails.getRoomName(), hostelV1.getHostelName(),Utils.getInitials(hostelV1.getHostelName()));
                }
            }

        } else {
            assert customers != null;
            bedHistory = customerBedHistoryService.getCustomerBookedBed(customers.getCustomerId());
            BedDetails bedDetails = bedService.getBedDetails(bedHistory.getBedId());
            if (bedDetails != null) {
                stayInfo = new StayInfo(bedDetails.getBedName(), bedDetails.getFloorName(), bedDetails.getRoomName(), hostelV1.getHostelName(),Utils.getInitials(hostelV1.getHostelName()));
            }
        }
        StringBuilder receiverfullName = new StringBuilder();
        Users createdBy = userService.findUserByUserId(transactionV1.getCreatedBy());

        if (createdBy.getFirstName() != null) {
            receiverfullName.append(createdBy.getFirstName());
        }
        if (createdBy.getLastName() != null && !createdBy.getLastName().trim().equalsIgnoreCase("")) {
            if (createdBy.getFirstName() != null) {
                receiverfullName.append(" ");
            }
            receiverfullName.append(createdBy.getLastName());
        }


        String paymentMode = "";

        if (bankingV1.getAccountType().equalsIgnoreCase(BankAccountType.CASH.name())) {
            paymentMode = "Cash";
        }
        else if (bankingV1.getAccountType().equalsIgnoreCase(BankAccountType.CARD.name())) {
            paymentMode = "Card";
        }
        else if (bankingV1.getAccountType().equalsIgnoreCase(BankAccountType.UPI.name())) {
            paymentMode = "Upi";
        }
        else if (bankingV1.getAccountType().equalsIgnoreCase(BankAccountType.BANK.name())) {
            paymentMode = "Bank";
        }
        ReceiptInfo receiptInfo = new ReceiptInfo(transactionV1.getTransactionReferenceId(), transactionV1.getTransactionId(), Utils.dateToString(transactionV1.getPaymentDate()), Utils.dateToTime(transactionV1.getPaymentDate()), transactionV1.getPaidAmount(), invoiceType, transactionV1.getReferenceNumber(), receiverfullName.toString(), invoiceMonth, paymentMode);


        double dueAmount = 0.0;
        if (invoicesV1.getPaidAmount() != null) {
            dueAmount = invoicesV1.getTotalAmount() - invoicesV1.getPaidAmount();
        }


        ReceiptDetails details = new ReceiptDetails(invoicesV1.getInvoiceNumber(), transactionV1.getTransactionReferenceId(), Utils.dateToString(invoicesV1.getInvoiceStartDate()), invoicesV1.getInvoiceId(), invoicesV1.getTotalAmount(), invoicesV1.getPaidAmount(), dueAmount, hostelEmail, hostelPhone, "91", receiptInfo, customerInfo, stayInfo, accountDetails, receiptConfigInfo);
        return new ResponseEntity<>(details, HttpStatus.OK);

    }

    public ResponseEntity<?> getInvoiceDetailsByInvoiceId(String hostelId, String invoiceId) {
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(Utils.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        String customerId = authentication.getName();
        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        HostelV1 hostelV1 = hostelRepository.findByHostelId(hostelId);
        if (hostelV1 == null) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        InvoicesV1 invoicesV1 = invoicesV1Repository.findById(invoiceId).orElse(null);
        if (invoicesV1 == null) {
            return new ResponseEntity<>(Utils.INVALID_INVOICE_ID, HttpStatus.BAD_REQUEST);
        }


        String paymentStatus = null;
        if (invoicesV1.getPaymentStatus() != null) {
            paymentStatus = InvoiceUtils.getInvoicePaymentStatusByStatus(invoicesV1.getPaymentStatus());
        }

        if (invoicesV1.isCancelled()) {
            paymentStatus = "Cancelled";
        }

        StringBuilder invoiceMonth = new StringBuilder();
        StringBuilder invoiceRentalPeriod = new StringBuilder();

        String hostelPhone = null;
        String hostelEmail = null;
        String invoiceType = "Rent";
        StringBuilder hostelFullAddress = new StringBuilder();
        String invoiceSignatureUrl = null;
        String hostelLogo = null;

        if (invoicesV1.getInvoiceType().equalsIgnoreCase(InvoiceType.ADVANCE.name())) {
            invoiceType = "Advance";
        } else if (invoicesV1.getInvoiceType().equalsIgnoreCase(InvoiceType.BOOKING.name())) {
            invoiceType = "Booking";
        }

        if (hostelV1.getHouseNo() != null && !hostelV1.getHouseNo().trim().equalsIgnoreCase("")) {
            hostelFullAddress.append(hostelV1.getHouseNo());
            hostelFullAddress.append(", ");
        }
        if (hostelV1.getStreet() != null && !hostelV1.getStreet().trim().equalsIgnoreCase("")) {
            hostelFullAddress.append(hostelV1.getStreet());
            hostelFullAddress.append(", ");
        }
        if (hostelV1.getCity() != null && !hostelV1.getCity().trim().equalsIgnoreCase("")) {
            hostelFullAddress.append(hostelV1.getCity());
            hostelFullAddress.append(", ");
        }
        if (hostelV1.getState() != null && !hostelV1.getState().trim().equalsIgnoreCase("")) {
            hostelFullAddress.append(hostelV1.getState());
            hostelFullAddress.append("-");
        }
        if (hostelV1.getPincode() != 0) {
            hostelFullAddress.append(hostelV1.getPincode());
        }

        if (invoicesV1.getInvoiceType().equalsIgnoreCase(InvoiceType.RENT.name()) || invoicesV1.getInvoiceType().equalsIgnoreCase(InvoiceType.REASSIGN_RENT.name())) {

            invoiceRentalPeriod.append(Utils.dateToDateMonth(invoicesV1.getInvoiceStartDate()));
            invoiceRentalPeriod.append("-");
            invoiceRentalPeriod.append(Utils.dateToDateMonth(invoicesV1.getInvoiceEndDate()));
            BillingDates billingDates = hostelConfigService.getBillingRuleByDateAndHostelId(hostelId, invoicesV1.getInvoiceStartDate());
            if (billingDates != null) {
                invoiceMonth.append(Utils.dateToMonth(billingDates.currentBillStartDate()));
            }
        }

        AccountDetails accountDetails = null;
        ConfigInfo signatureInfo = null;
        BillTemplates hostelTemplates = templatesService.getTemplateByHostelId(hostelId);
        if (hostelTemplates != null) {
            if (!hostelTemplates.isMobileCustomized()) {
                hostelPhone = hostelTemplates.getMobile();
            } else {
                if (invoicesV1.getInvoiceType().equalsIgnoreCase(InvoiceType.ADVANCE.name())) {
                    hostelPhone = hostelTemplates.getTemplateTypes().stream().filter(item -> item.getInvoiceType().equalsIgnoreCase(BillConfigTypes.ADVANCE.name())).map(BillTemplateType::getInvoicePhoneNumber).toList().getFirst();
                } else {
                    hostelPhone = hostelTemplates.getTemplateTypes().stream().filter(item -> item.getInvoiceType().equalsIgnoreCase(BillConfigTypes.RENTAL.name())).map(BillTemplateType::getInvoicePhoneNumber).toList().getFirst();
                }

            }

            if (!hostelTemplates.isEmailCustomized()) {
                hostelEmail = hostelTemplates.getEmailId();
            } else {
                if (invoicesV1.getInvoiceType().equalsIgnoreCase(InvoiceType.ADVANCE.name())) {
                    hostelEmail = hostelTemplates.getTemplateTypes().stream().filter(item -> item.getInvoiceType().equalsIgnoreCase(BillConfigTypes.ADVANCE.name())).map(BillTemplateType::getInvoiceMailId).toList().getFirst();
                } else {
                    hostelEmail = hostelTemplates.getTemplateTypes().stream().filter(item -> item.getInvoiceType().equalsIgnoreCase(BillConfigTypes.RENTAL.name())).map(BillTemplateType::getInvoiceMailId).toList().getFirst();
                }
            }

            BillTemplateType templateType = null;

            if (invoicesV1.getInvoiceType().equalsIgnoreCase(InvoiceType.ADVANCE.name())) {
                templateType = hostelTemplates.getTemplateTypes().stream().filter(item -> item.getInvoiceType().equalsIgnoreCase(BillConfigTypes.ADVANCE.name())).toList().getFirst();
            } else {
                templateType = hostelTemplates.getTemplateTypes().stream().filter(item -> item.getInvoiceType().equalsIgnoreCase(BillConfigTypes.RENTAL.name())).toList().getFirst();
            }

            if (!hostelTemplates.isSignatureCustomized()) {
                invoiceSignatureUrl = hostelTemplates.getDigitalSignature();
            } else {
                invoiceSignatureUrl = templateType.getInvoiceSignatureUrl();
            }
            if (!hostelTemplates.isLogoCustomized()) {
                hostelLogo = hostelTemplates.getHostelLogo();
            } else {
                hostelLogo = templateType.getInvoiceLogoUrl();
            }

            if (templateType.getBankAccountId() != null) {
                BankingV1 bankingV1 = bankingService.getBankDetails(templateType.getBankAccountId());
                accountDetails = new AccountDetails(bankingV1.getAccountNumber(), bankingV1.getIfscCode(), bankingV1.getBankName(), bankingV1.getUpiId(), templateType.getQrCode());
            } else {
                accountDetails = new AccountDetails(null, null, null, null, templateType.getQrCode());
            }


            signatureInfo = new ConfigInfo(templateType.getInvoiceTermsAndCondition(), invoiceSignatureUrl, hostelLogo, hostelFullAddress.toString(), templateType.getInvoiceTemplateColor(), templateType.getInvoiceNotes(), invoiceType);
        }

        Customers customers = customerService.getCustomerInformation(invoicesV1.getCustomerId());
        CustomerInfo customerInfo = null;
        if (customers != null) {
            StringBuilder fullName = new StringBuilder();
            StringBuilder fullAddress = new StringBuilder();
            if (customers.getFirstName() != null) {
                fullName.append(customers.getFirstName());
            }
            if (customers.getLastName() != null && !customers.getLastName().trim().equalsIgnoreCase("")) {
                fullName.append(" ");
                fullName.append(customers.getLastName());
            }
            if (customers.getHouseNo() != null) {
                fullAddress.append(customers.getHouseNo());
                fullAddress.append(", ");
            }
            if (customers.getStreet() != null) {
                fullAddress.append(customers.getStreet());
                fullAddress.append(", ");
            }
            if (customers.getCity() != null) {
                fullAddress.append(customers.getCity());
                fullAddress.append(", ");
            }
            if (customers.getState() != null) {
                fullAddress.append(customers.getState());
                fullAddress.append("-");
            }

            if (customers.getPincode() != 0) {
                fullAddress.append(customers.getPincode());
            }

            customerInfo = new CustomerInfo(customers.getFirstName(), customers.getLastName(), fullName.toString(), customers.getCustomerId(), customers.getMobile(), "91", fullAddress.toString(), customers.getHouseNo(), customers.getStreet(), customers.getCity(), customers.getState(), customers.getPincode(), Utils.dateToString(customers.getJoiningDate()));
        }

        StayInfo stayInfo = null;
        CustomersBedHistory bedHistory = customerBedHistoryService.getCustomerBedByStartDate(customers.getCustomerId(), invoicesV1.getInvoiceStartDate(), invoicesV1.getInvoiceEndDate());

        if (bedHistory != null) {
            BedDetails bedDetails = bedService.getBedDetails(bedHistory.getBedId());
            if (bedDetails != null) {
                stayInfo = new StayInfo(bedDetails.getBedName(), bedDetails.getFloorName(), bedDetails.getRoomName(), hostelV1.getHostelName(),Utils.getInitials(hostelV1.getHostelName()));
            }
        }else {
            stayInfo = new StayInfo(null, null, null, hostelV1.getHostelName(),Utils.getInitials(hostelV1.getHostelName()));
        }

        if (invoicesV1.getInvoiceType().equalsIgnoreCase(InvoiceType.SETTLEMENT.name())) {
            double paidAmount = transactionService.findPaidAmountForInvoice(invoiceId);
            double balanceAmount = invoicesV1.getTotalAmount() - paidAmount;
            List<String> invoicesList = invoicesV1.getCancelledInvoices();
            List<com.smartstay.tenant.response.invoices.InvoiceItems> listInvoiceItems = new ArrayList<>();
            listInvoiceItems.add(new com.smartstay.tenant.response.invoices.InvoiceItems(invoicesV1.getInvoiceNumber(), InvoiceType.SETTLEMENT.name(), invoicesV1.getBasePrice()));
            List<com.smartstay.tenant.dao.Deductions> listDeductions = invoicesV1.getInvoiceItems().stream().map(i -> {
                com.smartstay.tenant.dao.Deductions d = new com.smartstay.tenant.dao.Deductions();
                if (i.getInvoiceItem().equalsIgnoreCase(com.smartstay.tenant.ennum.InvoiceItems.OTHERS.name())) {
                    if (i.getOtherItem() != null) {
                        i.setInvoiceItem(i.getOtherItem());
                    }
                } else {
                    i.setInvoiceItem(i.getInvoiceItem());
                }
                d.setType(i.getInvoiceItem());
                d.setAmount(i.getAmount());

                return d;
            }).toList();

            double totalDeductionAmount = invoicesV1.getInvoiceItems().stream().mapToDouble(com.smartstay.tenant.dao.InvoiceItems::getAmount).sum();


            InvoiceInfo invoiceInfo = new InvoiceInfo(invoicesV1.getBasePrice(), 0.0, 0.0, invoicesV1.getTotalAmount(), paidAmount, balanceAmount, invoiceRentalPeriod.toString(), invoiceMonth.toString(), paymentStatus, invoicesV1.isCancelled(), totalDeductionAmount, listInvoiceItems, listDeductions);
            List<InvoiceSummary> invoiceSummaries = invoicesV1Repository.findInvoiceSummariesByHostelId(hostelId, invoicesList);
            FinalSettlementResponse finalSettlementResponse = new FinalSettlementResponse(invoicesV1.getInvoiceNumber(), invoicesV1.getInvoiceId(), Utils.dateToString(invoicesV1.getInvoiceStartDate()), Utils.dateToString(invoicesV1.getInvoiceDueDate()), hostelEmail, hostelPhone, "91", InvoiceType.SETTLEMENT.name(), customers.getHostelId(), customerInfo, stayInfo, accountDetails, signatureInfo, invoiceSummaries, invoiceInfo);
            return new ResponseEntity<>(finalSettlementResponse, HttpStatus.OK);

        }

        Double subTotal = 0.0;
        Double paidAmount = 0.0;
        Double balanceAmount = 0.0;

        paidAmount = transactionService.findPaidAmountForInvoice(invoiceId);
        balanceAmount = invoicesV1.getTotalAmount() - paidAmount;
        subTotal = invoicesV1.getTotalAmount();
        List<com.smartstay.tenant.response.invoices.InvoiceItems> listInvoiceItems = new ArrayList<>();

        for (com.smartstay.tenant.dao.InvoiceItems item : invoicesV1.getInvoiceItems()) {
            String description;
            switch (item.getInvoiceItem()) {
                case "RENT" -> description = "Rent";
                case "ADVANCE" -> description = "Advance";
                case "EB" -> description = "Electricity Bill";
                case "AMENITY" -> description = "Amenity";
                case "OTHERS" -> description = item.getOtherItem() != null ? item.getOtherItem() : "Others";
                default -> description = Utils.capitalize(item.getInvoiceItem());
            }
            com.smartstay.tenant.response.invoices.InvoiceItems responseItem = new com.smartstay.tenant.response.invoices.InvoiceItems(invoicesV1.getInvoiceNumber(), description, item.getAmount());

            listInvoiceItems.add(responseItem);
        }
        List<PaymentHistoryProjection> paymentHistoryList = transactionService.getPaymentHistoryByInvoiceId(invoiceId);


        InvoiceInfo invoiceInfo = new InvoiceInfo(subTotal, 0.0, 0.0, invoicesV1.getTotalAmount(), paidAmount, balanceAmount, invoiceRentalPeriod.toString(), invoiceMonth.toString(), paymentStatus, invoicesV1.isCancelled(), 0.0, listInvoiceItems, null);

        InvoiceDetails details = new InvoiceDetails(invoicesV1.getInvoiceNumber(), invoicesV1.getInvoiceId(), Utils.dateToString(invoicesV1.getInvoiceStartDate()), Utils.dateToString(invoicesV1.getInvoiceDueDate()), hostelEmail, hostelPhone, "91", customers.getHostelId(), customerInfo, stayInfo, invoiceInfo, accountDetails, paymentHistoryList, signatureInfo);
        return new ResponseEntity<>(details, HttpStatus.OK);

    }

}
