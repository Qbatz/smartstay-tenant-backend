package com.smartstay.tenant.controller;


import com.smartstay.tenant.service.InvoiceService;
import com.smartstay.tenant.service.TransactionService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> getInvoicesList(@PathVariable String hostelId) {
        return invoiceService.getInvoiceList(hostelId);
    }



    @GetMapping("/{hostelId}/{invoiceId}")
    public ResponseEntity<?> getInvoiceById(@PathVariable String hostelId, @PathVariable String invoiceId) {
        return invoiceService.getInvoicesById(hostelId,invoiceId);
    }


    @GetMapping("payment-list/{hostelId}")
    public ResponseEntity<?> getPaymentList(@PathVariable String hostelId) {
        return transactionService.getTransactionList(hostelId);
    }
}
