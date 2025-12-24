package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.*;
import com.smartstay.tenant.dto.BillingDates;
import com.smartstay.tenant.dto.invoice.*;
import com.smartstay.tenant.dto.invoice.Deductions;
import com.smartstay.tenant.ennum.InvoiceType;
import com.smartstay.tenant.mapper.invoice.InvoiceItemMapper;
import com.smartstay.tenant.mapper.invoice.InvoiceSummaryMapper;
import com.smartstay.tenant.repository.InvoicesV1Repository;
import com.smartstay.tenant.response.dashboard.InvoiceSummaryResponse;
import com.smartstay.tenant.response.hostel.InvoiceItems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    private HostelService hostelService;


    public List<InvoiceItems> getInvoicesWithItems(String customerId, Date startDate, Date endDate) {
        return invoicesV1Repository.getInvoiceItemDetails(customerId, startDate, endDate, List.of(InvoiceType.EB.name(), InvoiceType.RENT.name()));
    }

    public InvoiceSummaryResponse getLatestInvoiceSummary(String hostelId, String customerId, Date startDate, Date endDate) {
        InvoiceSummaryProjection projection = invoicesV1Repository.getInvoiceSummary(hostelId, customerId, startDate, endDate);
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
        List<InvoiceItemProjection> invoiceItems = invoicesV1Repository.getAllInvoiceItems(hostelId, customerId);
        InvoiceItemMapper invoiceItemMapper = new InvoiceItemMapper();

        List<InvoiceItemResponseDTO> invoiceItemDTOs = invoiceItems.stream().map(invoiceItemMapper).toList();
        if (invoiceItemDTOs.isEmpty()) {
            return new ResponseEntity<>(Utils.INVOICE_ITEMS_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(invoiceItemDTOs, HttpStatus.OK);

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
                listDeductions = advance.getDeductions()
                        .stream()
                        .map(i -> new Deductions(i.getType(), i.getAmount()))
                        .toList();
            }
        }

        totalAdvancePaid = advancePaid + bookingAmount;
        advanceInfo = new AdvanceInfo(advanceAmount,
                advancePaid,
                advanceInvoiceNumber,
                bookingAmount,
                totalAdvancePaid,
                listDeductions);

        BillingDates billingDates = hostelService.getBillStartDate(invoice.getHostelId(), invoice.getInvoiceStartDate());
        List<InvoicesV1> currentMonthRentalInvoices = null;
        if (billingDates != null) {
            currentMonthRentalInvoices = invoicesV1Repository.findCurrentMonthInvoices(customerId, billingDates.currentBillStartDate(), billingDates.currentBillEndDate());
        }

        CurrentMonthInfo currentMonthInfo = null;

        finalSettlementDetails = new FinalSettlementDetails(invoice.getInvoiceId(), invoice.getInvoiceNumber(), Utils.capitalize(invoice.getInvoiceType()), invoice.getInvoiceGeneratedDate(), invoice.getInvoiceDueDate(), invoice.getInvoiceStartDate(), invoice.getInvoiceEndDate(), invoice.getTotalAmount(), totalPaid, dueAmount, status, invoice.getGst(), invoice.getCgst(), invoice.getSgst(), invoice.getGstPercentile(), invoiceItems, receipts, advanceInfo, currentMonthInfo, lastPaidDate, lastPaymentMode, referenceId, showMessage);

        return new ResponseEntity<>(finalSettlementDetails, HttpStatus.OK);
    }

    public InvoiceDetailsDTO getInvoiceDetails(String invoiceId, InvoicesV1 invoice) {

        List<InvoiceItemDTO> invoiceItems = invoicesV1Repository.getInvoiceItems(invoiceId);

        Double totalPaid = transactionService.getTotalPaidAmountByInvoiceId(invoiceId);
        if (totalPaid == null) totalPaid = 0.0;

        double dueAmount = invoice.getTotalAmount() - totalPaid;

        String status = dueAmount == 0 ? "Paid" : totalPaid == 0 ? "Pending" : "Partially Paid";

        List<ReceiptDTO> receipts = transactionService.getReceiptsByInvoiceId(invoiceId);

        TransactionV1 latestTransaction = transactionService.getLatestTransactionByInvoiceId(invoiceId);

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

        return new InvoiceDetailsDTO(invoice.getInvoiceId(), invoice.getInvoiceNumber(), Utils.capitalize(invoice.getInvoiceType()), invoice.getInvoiceGeneratedDate(), invoice.getInvoiceDueDate(), invoice.getInvoiceStartDate(), invoice.getInvoiceEndDate(), invoice.getTotalAmount(), totalPaid, dueAmount, status, invoice.getGst(), invoice.getCgst(), invoice.getSgst(), invoice.getGstPercentile(), invoiceItems, receipts, lastPaidDate, lastPaymentMode, referenceId, showMessage);
    }

}
