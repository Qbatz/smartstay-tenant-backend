package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.config.RestTemplateLoggingInterceptor;
import com.smartstay.tenant.dao.BankingV1;
import com.smartstay.tenant.dao.TransactionV1;
import com.smartstay.tenant.dto.TransactionDto;
import com.smartstay.tenant.dto.bills.PaymentHistoryProjection;
import com.smartstay.tenant.dto.invoice.ReceiptDTO;
import com.smartstay.tenant.mapper.TransactionForCustomerDetailsMapper;
import com.smartstay.tenant.repository.BankingV1Repository;
import com.smartstay.tenant.repository.TransactionV1Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
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

    @Value("${REPORTS_URL}")
    private String reportsUrl;
    private final RestTemplate restTemplate;

    public TransactionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        restTemplate.setInterceptors(Collections.singletonList(new RestTemplateLoggingInterceptor()));
    }

    public TransactionV1 getTransactionById(String transactionId) {
        return transactionV1Repository.findById(transactionId).orElse(null);
    }

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
        return transactionV1Repository.findTopByInvoiceIdOrderByPaidAtDesc(invoiceId);
    }

    public Double findPaidAmountForInvoice(String invoiceId) {
        List<TransactionV1> listTransaction = transactionV1Repository.findByInvoiceId(invoiceId);
        double paidAmount = 0.0;
        if (!listTransaction.isEmpty()) {

            paidAmount = listTransaction.stream()
                    .mapToDouble(i -> {
                        if (i.getPaidAmount() == null) {
                            return 0.0;
                        }
                        return i.getPaidAmount();
                    })
                    .sum();
        }
        return paidAmount;
    }

    public List<PaymentHistoryProjection> getPaymentHistoryByInvoiceId(String invoiceId) {
        return transactionV1Repository.getPaymentHistoryByInvoiceId(invoiceId);
    }


    public List<TransactionV1> getAllTransactionsByInvoiceId(String invoiceId) {
        return transactionV1Repository.findByInvoiceId(invoiceId);
    }

    public ResponseEntity<?> downloadPdf(String hostelId, String receiptId) {

        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Utils.UNAUTHORIZED);
        }

        String customerId = authentication.getName();
        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        TransactionV1 transactionV1 = transactionV1Repository.getReferenceById(receiptId);
        if (transactionV1 != null) {
            if (transactionV1.getTransactionId() == null) {
                return new ResponseEntity<>(Utils.INVALID_TRANSACTION_ID, HttpStatus.BAD_REQUEST);
            }
            if (transactionV1.getReceiptUrl() != null) {
                return new ResponseEntity<>(transactionV1.getReceiptUrl(), HttpStatus.OK);
            }
            else {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                String endpoint = reportsUrl + "/v2/reports/receipts/"+ hostelId + "/" +  receiptId;
                HttpEntity<Void> request =
                        new HttpEntity<>(headers);

                ResponseEntity<String> response = restTemplate.exchange(
                        endpoint,
                        HttpMethod.GET,
                        request,
                        String.class
                );

                if (response.getStatusCode() == HttpStatus.OK) {
                    transactionV1.setReceiptUrl(response.getBody());
                    transactionV1Repository.save(transactionV1);
                    return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
                }
                else {
                    return new ResponseEntity<>(Utils.TRY_AGAIN, HttpStatus.BAD_REQUEST);
                }
            }
        }
        else {
            return new ResponseEntity<>(Utils.INVALID_TRANSACTION_ID, HttpStatus.BAD_REQUEST);
        }
    }
}
