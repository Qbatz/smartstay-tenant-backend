package com.smartstay.tenant.controller;

import com.smartstay.tenant.service.InvoiceService;
import com.smartstay.tenant.service.TransactionService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("v2/invoices")
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@SecurityRequirement(name = "Authorization")
@CrossOrigin("*")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/{hostelId}")
    public ResponseEntity<?> getInvoicesList(@PathVariable String hostelId,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date startDate,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date endDate,
                                             @RequestParam(required = false) String dateFilterOption) {
        return invoiceService.getInvoiceList(hostelId, startDate, endDate, dateFilterOption);
    }

    @GetMapping("receipt-details/{hostelId}/{transactionId}")
    public ResponseEntity<?> getReceiptDetails(@PathVariable("hostelId") String hostelId,
                                               @PathVariable("transactionId") String transactionId) {
        return invoiceService.getReceiptDetailsByTransactionId(hostelId, transactionId);
    }

    @GetMapping("invoice-details/{hostelId}/{invoiceId}")
    public ResponseEntity<?> getInvoiceInfo(@PathVariable("hostelId") String hostelId,
                                            @PathVariable("invoiceId") String invoiceId) {
        return invoiceService.getInvoiceDetailsByInvoiceId(hostelId, invoiceId);
    }

    @GetMapping("/{hostelId}/{invoiceId}")
    public ResponseEntity<?> getInvoiceById(@PathVariable String hostelId,
                                            @PathVariable String invoiceId) {
        return invoiceService.getInvoicesById(hostelId,invoiceId);
    }

    @GetMapping("payment-list/{hostelId}")
    public ResponseEntity<?> getPaymentList(@PathVariable String hostelId) {
        return transactionService.getTransactionList(hostelId);
    }

    @GetMapping("/pdf/{hostelId}/{invoiceId}")
    public ResponseEntity<?> downloadInvoicePdf(@PathVariable("hostelId") String hostelId,
                                                @PathVariable("invoiceId") String invoiceId) {
        return invoiceService.downloadPdf(hostelId, invoiceId);
    }

    @GetMapping("/pdf/receipts/{hostelId}/{receiptId}")
    public ResponseEntity<?> downloadReceiptPdf(@PathVariable("hostelId") String hostelId,
                                                @PathVariable("receiptId") String receiptId) {
        return transactionService.downloadPdf(hostelId, receiptId);
    }
}


