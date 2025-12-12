package com.smartstay.tenant.service;


import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.BankingV1;
import com.smartstay.tenant.dao.TransactionV1;
import com.smartstay.tenant.dto.TransactionDto;
import com.smartstay.tenant.dto.invoice.ReceiptDTO;
import com.smartstay.tenant.mapper.TransactionForCustomerDetailsMapper;
import com.smartstay.tenant.repository.BankingV1Repository;
import com.smartstay.tenant.repository.TransactionV1Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionV1Repository transactionV1Repository;

    @Autowired
    private BankingV1Repository bankingV1Repository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private Authentication authentication;


    public ResponseEntity<?> getTransactionList(String hostelId) {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Utils.UNAUTHORIZED);
        }
        String customerId = authentication.getName();
        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        List<TransactionDto> transactionDtos = getTransactionInfoByCustomerId(customerId, hostelId);
        if (transactionDtos.isEmpty()) {
            return new ResponseEntity<>(Utils.PAYMENTS_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(transactionDtos, HttpStatus.OK);

    }

    public List<TransactionDto> getTransactionInfoByCustomerId(String customerId, String hostelId) {
        if (authentication.isAuthenticated()) {
            List<TransactionV1> listTransactions = transactionV1Repository.findByCustomerIdAndHostelId(customerId, hostelId);

            return listTransactions.stream().map(i -> new TransactionForCustomerDetailsMapper().apply(i)).toList();
        }
        return new ArrayList<>();
    }

    public List<ReceiptDTO> getReceiptsByInvoiceId(String invoiceId) {
        List<ReceiptDTO> receipts = transactionV1Repository.getReceiptsByInvoiceId(invoiceId);

        for (ReceiptDTO r : receipts) {
            if (r.getPaymentMode() != null) {
                r.setPaymentMode(Utils.capitalize(r.getPaymentMode()));
            }
        }

        return receipts;
    }


    public Double getTotalPaidAmountByInvoiceId(String invoiceId) {
        return transactionV1Repository.getTotalPaid(invoiceId);
    }

    public BankingV1 getBankDetailsById(String bankId) {
        return bankingV1Repository.findById(bankId).orElse(null);
    }

    public TransactionV1 getLatestTransactionByInvoiceId(String invoiceId) {
        return transactionV1Repository.findLatestTransaction(invoiceId);
    }


}
