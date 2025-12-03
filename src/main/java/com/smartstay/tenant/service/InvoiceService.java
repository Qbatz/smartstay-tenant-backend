package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.InvoicesV1;
import com.smartstay.tenant.dto.invoice.InvoiceDetailsDTO;
import com.smartstay.tenant.dto.invoice.InvoiceItemDTO;
import com.smartstay.tenant.dto.invoice.InvoiceItemResponseDTO;
import com.smartstay.tenant.dto.invoice.ReceiptDTO;
import com.smartstay.tenant.ennum.InvoiceType;
import com.smartstay.tenant.repository.InvoicesV1Repository;
import com.smartstay.tenant.response.dashboard.InvoiceSummaryResponse;
import com.smartstay.tenant.response.hostel.InvoiceItems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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


    public List<InvoiceItems> getInvoicesWithItems(String customerId, Date startDate, Date endDate) {
        return invoicesV1Repository.getInvoiceItemDetails(customerId, startDate, endDate, List.of(InvoiceType.EB.name(), InvoiceType.RENT.name()));
    }

    public InvoiceSummaryResponse getLatestInvoiceSummary(String customerId, Date startDate, Date endDate) {
        Pageable limitOne = PageRequest.of(0, 1);
        return invoicesV1Repository.getInvoiceSummary(customerId, startDate, endDate, limitOne).stream().findFirst().orElse(null);
    }


    public ResponseEntity<?> getInvoiceList(String hostelId) {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Utils.UNAUTHORIZED);
        }
        String customerId = authentication.getName();
        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        List<InvoiceItemResponseDTO> invoiceItems = invoicesV1Repository.getAllInvoiceItems(hostelId, customerId);
        if (invoiceItems.isEmpty()) {
            return new ResponseEntity<>(Utils.INVOICE_ITEMS_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(invoiceItems, HttpStatus.OK);

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
        InvoiceDetailsDTO invoiceItem = getInvoiceDetails(invoiceId, invoice);
        if (invoiceItem == null) {
            return new ResponseEntity<>(Utils.INVOICE_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(invoiceItem, HttpStatus.OK);

    }


    public InvoiceDetailsDTO getInvoiceDetails(String invoiceId, InvoicesV1 invoice) {

        List<InvoiceItemDTO> invoiceItems = invoicesV1Repository.getInvoiceItems(invoiceId);

        Double totalPaid = transactionService.getTotalPaidAmountByInvoiceId(invoiceId);
        if (totalPaid == null) totalPaid = 0.0;

        double dueAmount = invoice.getTotalAmount() - totalPaid;

        String status = dueAmount == 0 ? "Paid" : totalPaid == 0 ? "Pending" : "Partially Paid";

        List<ReceiptDTO> receipts = transactionService.getReceiptsByInvoiceId(invoiceId);

        return new InvoiceDetailsDTO(invoice.getInvoiceId(), invoice.getInvoiceNumber(), Utils.capitalize(invoice.getInvoiceType()), invoice.getInvoiceGeneratedDate(), invoice.getInvoiceDueDate(), invoice.getTotalAmount(), totalPaid, dueAmount, status, invoice.getGst(), invoice.getCgst(), invoice.getSgst(), invoice.getGstPercentile(), invoiceItems, receipts);
    }

}
